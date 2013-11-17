(ns oyster.map
  (:require [oyster.tiles :as t]
            [oyster.random :as r]))

(defn grid
  "Generate wxh of character c."
  [w h c]
  (->> c
       (repeat w)
       (repeat h)))

(defn transpose
  "Transpose a list-of-lists matrix."
  [m]
  (apply map vector m))

(defn to-vec
  "Convert a list-of-lists to a vec-of-vecs."
  [ls]
  (vec (map vec ls)))

(defn overwrite-after
  "Overwrite all elements of coll after the nth with e.
  (overwrite-after :e 3 [0 1 2 3 4 5 6])
  ;=> [0 1 2 :e :e :e :e]"
  [e n coll]
  (vec (concat (take n coll) (repeat (- (count coll) n) e))))

(defn frontier
  "Generate random frontier. Returns y-distances from top
  of map for the last non-water tile of each cell."
  [rng width init-height]
  (let [n init-height
        jaggedness 1
        deltas (repeatedly width #(r/rand-int rng (- jaggedness) (inc jaggedness)))]
    (reductions + n deltas)))

(defn draw-frontier
  "Draw frontier in ascii map."
  [m frontier c]
  (let [mt (transpose m)
        r (map (fn [mcol y] (overwrite-after c y mcol)) mt frontier)]
    (transpose r)))

(defn as-chars
  "Represent a map as nested array of ascii characters,
  which can be displayed by the UI."
  [{coastline :coastline
    cliff :cliff
    width :width
    height :height}]
  (let [m (grid width height :grass)]
    (-> m
        (draw-frontier cliff :beach)
        (draw-frontier coastline :water)
        to-vec)))
    
(defn empty-map
  "Generate random empty map from seed."
  [seed]
  (let [rng (if seed (r/rng seed) (r/rng))
        w 70
        h 35
        coast (frontier rng w (- h 5))
        cliff (frontier rng w (- h 10))]
    (as-chars {:width w :height h :coastline coast :cliff cliff})))

(defn tile-description
  [m [r c]]
  (:description
    (t/all-tiles
      (get-in m [r c] :nothing))))
