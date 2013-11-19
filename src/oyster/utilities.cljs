(ns oyster.utilities)

(defn window-href
  []
  (.-href (.-location js/window)))

(defn extract-value
  [coll]
  (cond
    (empty? coll) nil
    (= 1 (count coll)) (first coll)
    :else coll))

(defn query-value
  "Return the value of the query parameter identified by key."
  [key]
  (->
    (str "[\\?&]" (name key) "=([^&#]*)")
    re-pattern
    (re-seq (window-href))
    ((partial map second))
    extract-value))
