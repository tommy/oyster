(ns oyster.core
  (:use [cljs.core.async :only [put! <!! <! chan timeout map< filter< remove<]])
  (:require [clojure.browser.event :as event]
            [goog.string.format :as gformat]
            [goog.string :as gstring]
            [oyster.dom :as dom :refer [length item as-seq
                                        set-html! set-style! by-id by-class
                                        by-tag html update-html! update-height!]]
            [oyster.view]
            [oyster.async :as async :refer [listen every process-channel bind]]
            [oyster.map :as m])
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]]))

(. js/console (log "Oyster Cloyster."))

(defn seed [] 1234)

(defn selected-tiles
  "A channel populated with selected map tiles.
  (Tiles are selected by hovering.)"
  []
  (->> (listen (by-id :map) :mouseover)
       (map< #(.-target %))
       (remove< #(nil? (.-tile (.-attributes %))))
       (map< #(.-value (.-tile (.-attributes %))))))

;; render page
(defn game []
  (let [m (m/empty-map (seed))]
    (set-html! (by-id :content) (.-outerHTML (oyster.view/main-game m)))
    (let [tiles (selected-tiles)]
      (process-channel print tiles))))

;; begin the game when everything is loaded
(set! (.-onload js/window) game)
