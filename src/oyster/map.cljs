(ns oyster.map)

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

(defn overwrite-after
  "Overwrite all elements of coll after the nth with e.
  (overwrite-after :e 3 [0 1 2 3 4 5 6])
  ;=> [0 1 2 :e :e :e :e]"
  [e n coll]
  (vec (concat (take n coll) (repeat (- (count coll) n) e))))

(defn coastline
  "Generate random coastline. Returns y-distances from top
  of map for the last non-water tile of each cell."
  [seed width]
  (repeat width 20)) ;; TODO

(defn gen-coastline
  "Draw coastline in ascii map."
  [m coast]
  (let [mt (transpose m)]
    (let [r (map
              (fn [mcol y] (overwrite-after \~ y mcol))
              mt
              coast)]
      (transpose r))))

(defn as-chars
  "Represent a map as nested array of ascii characters,
  which can be displayed by the UI."
  [{coastline :coastline
    width :width
    height :height}]
  (let [m (grid width height \.)]
    (gen-coastline m coastline)))
    


(defn empty-map
  "Generate random empty map from seed."
  [seed]
  (let [w 30
        h 25
        coast (coastline seed w)]
    (as-chars {:width w :height h :coastline coast})))
