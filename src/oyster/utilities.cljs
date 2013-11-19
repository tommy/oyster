(ns oyster.utilities)

(defn log [x] (.log js/console x))

;; println calls should log to the JS console.
(set! *print-fn* log)

(defn window-href [] (-> js/window .-location .-href))

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
