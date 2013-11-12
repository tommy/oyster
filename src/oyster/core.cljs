(ns oyster.core
  (:use [cljs.core.async :only [put! <!! <! chan timeout]])
  (:require [clojure.browser.event :as event]
            [goog.string.format :as gformat]
            [goog.string :as gstring]
            [oyster.dom :as dom :refer [length item as-seq
                                          set-html! set-style! by-id
                                          by-tag html update-html! update-height!]]
            [oyster.view]
            [oyster.async :as async :refer [listen every process-channel bind]]
            [oyster.map :as m])
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]]))

(. js/console (log "Oyster Cloyster."))

(defn seed [] 1234)

;; render page
(set-html! (. js/document -body) (. (oyster.view/draw-map (m/empty-map (seed))) -outerHTML))