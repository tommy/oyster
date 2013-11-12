(ns oyster.map)

(defn empty-map
  "Generate random empty map from seed."
  [seed]
  (repeat 10
          (repeat 10 \.)))
