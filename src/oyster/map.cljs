(ns oyster.map
  (:require [oyster.tiles :as t]
            [oyster.random :as r]
            [oyster.view :as view]
            [oyster.utilities :as util :refer [log]])
  (:require-macros [dommy.macros :as dommy]
                   [cljs.core.async.macros :refer [go]]))

;; matrix/seq functions

(def to-vec (partial mapv vec))

(defn dim
  "The dimension of matrix m. Returned as
  [number-of-rows number-of-cols]."
  [m]
  {:pre (apply = (map count m))} ;; should be a rectangular matrix
  [(count m) (count (first m))])

(defn matrix-indices
  "Generate an rxc matrix whose elemnents are their own indices.
  
  With no args, generate an infinite lazy seq of inifinite lazy seqs.
  For use with mmap."
  ([]
   (for [i (range)]
     (for [j (range)]
       [i j])))
  ([r c]
   (for [i (range r)]
    (for [j (range c)]
      [i j]))))

(defn mmap
  "Matrix map function. Accepts a variable number of matrices ms.
  
  Behaves like map when given args of unequal dimension, i.e., truncates."
  [f & ms]
  (apply map (partial map f) ms))

(defn mmap-indexed
  "Like mmap, but the first arg to f is a vector of that locations indices."
  [f & ms]
  (apply mmap f (matrix-indices) ms))

(defn grid
  "Generate wxh grid using the generator fn f."
  [w h f]
  (take h (partition w (repeatedly f))))

(defn transpose
  "Transpose a list-of-lists matrix."
  [m]
  (apply mapv vector m))

(defn overwrite-after
  "Overwrite all elements of coll after the nth with e.
  (overwrite-after :e 3 [0 1 2 3 4 5 6])
  ;=> [0 1 2 :e :e :e :e]"
  [e n coll]
  (vec (concat
         (take n coll)
         (repeat (- (count coll) n) e))))

;; feature generation

(defn frontier
  "Generate random frontier. Returns y-distances from top
  of map for the last non-water tile of each cell."
  [rng width init-height]
  (let [n init-height
        jaggedness 1
        deltas (repeatedly width #(r/rand-int rng (- jaggedness) (inc jaggedness)))]
    (reductions + n deltas)))

;; map "drawing"

(defn draw-frontier
  "Draw frontier in keyword raster."
  [m frontier c]
  (let [mt (transpose m)
        r (mapv (fn [mcol y] (overwrite-after c y mcol)) mt frontier)]
    (transpose r)))

(defn keyword-raster
  "Represent a map as nested array of tile keywords."
  [{coastline :coastline
    cliff :cliff
    width :width
    height :height}]
  (let [m (grid width height (constantly :grass))]
    (-> m
        (draw-frontier cliff :beach)
        (draw-frontier coastline :water))))

;; map creation

(defn random-keyword-map
  "Generate random empty map from seed."
  [seed]
  (let [rng (if seed (r/rng seed) (r/rng))
        w 80
        h 35
        coast (frontier rng w (+ (- h 5) (r/rand-int rng -5 5)))
        cliff (frontier rng w (+ (- h 10) (r/rand-int rng -5 5)))]
    (keyword-raster {:width w :height h :coastline coast :cliff cliff})))

(defrecord Tile [index type binding-chan])

(defn random-tile-map
  [seed]
  (let [mkeys (random-keyword-map seed)
        mels (mmap view/map-cell (matrix-indices) mkeys)
        mchans (mmap view/binding-chan mels)
        mtiles (mmap ->Tile (matrix-indices) (mmap atom mkeys) mchans)]
    {:tiles (to-vec mtiles)
     :el (view/draw-map mels)}))

;; map utilties

(defn tile-description
  [m [r c]]
  (-> m
      (get-in [r c] :nothing)
      :type
      deref
      t/all-tiles
      :description))

(defn change-tile
  [t newtype]
  (go
    (reset! (:type t) newtype)
    (>! (:binding-chan t) newtype)))
