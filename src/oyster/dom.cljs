(ns oyster.dom
  (:require [goog.style]))

;; println calls should log to the JS console.
(set! *print-fn* (fn [x] (.log js/console x)))
  
;; DOM utilities
  
(defn length [nodes] (. nodes -length))

(defn item [nodes n] (.item nodes n))

(defn as-seq [nodes]
  (for [i (range (length nodes))] (item nodes i)))

(defn set-html! [dom content]
  (set! (. dom -innerHTML) content))

(defn set-style! [dom attr value]
  (goog.style.setStyle dom attr value))

(defn by-id [id]
  (.getElementById js/document (name id)))

(defn by-tag [tag]
  (as-seq
    (.getElementsByTagName js/document (name tag))))

(defn html [dom] (. dom -innerHTML))
