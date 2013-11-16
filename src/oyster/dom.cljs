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

(defn by-class [class]
  (as-seq
    (.getElementsByClassName js/document (name class))))

(defn html [dom] (. dom -innerHTML))


;; DOM binding utilities

(defn update-html!
  "Used as an atom watch function.
  Updates the inner HTML of an element to the new-value of the atom."
  [id _ _ new-value]
  (set-html! (by-id id) new-value))

(defn update-height!
  "Used as an atom watch function.
  Updates the (percentage) height of an element to the new-value of the atom."
  [id _ _ new-value]
  (set-style! (by-id id) "height" (goog.string/format "%d%%" new-value)))
