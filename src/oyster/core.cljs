(ns oyster.core
  (:use [cljs.core.async :only [put! <!! <! chan timeout]])
  (:require [clojure.browser.event :as event]
            [goog.string.format :as gformat]
            [goog.string :as gstring]
            [oyster.dom :as dom :refer [length item as-seq
                                          set-html! set-style! by-id
                                          by-tag html]]
            [oyster.view]
            [oyster.async :as async :refer [listen every process-channel bind]])
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]]))

(. js/console (log "Oyster Cloyster."))

(. js/console (log (oyster.view/main-body)))
;; render page
(set-html! (. js/document -body) (. (oyster.view/main-body) -outerHTML))

;; view binding
(defn update-html!
  [id _ _ new-value]
  (set-html! (by-id id) new-value))

(defn update-height!
  [id _ _ new-value]
  (set-style! (by-id id) "height" (goog.string/format "%d%%" new-value)))

;; game state
(def oysters (atom 0))
(def income (atom 1))
(def hunger (atom 0))

(add-watch oysters :oysters update-html!)
(add-watch hunger :hunger update-height!)
     
;; state helpers
(defn curr-income [] @income)
      
;; Income
(let [c (every 1000 curr-income)]
  (process-channel c #(swap! oysters (partial + %))))
    
;; Hunger
(let [c (every 2000 (constantly 1))]
  (process-channel c (fn [v] (swap! hunger #(min 100 (+ v %))))))

;; Eat oysters
(let [el (by-id :eat-button)]
  (bind el :click (fn []
                    (do
                      (let [v @oysters]
                        (reset! oysters 0)
                        (swap! hunger #(max 0 (- % v))))))))


;; Throw them away
(let [el (by-id :throw-button)]
  (bind el :click #(swap! oysters (fn [x] (- x 10)))))
