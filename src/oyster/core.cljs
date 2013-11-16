(ns oyster.core
  (:use [cljs.core.async :only [put! <!! <! chan timeout map< filter< remove< mult]])
  (:require [clojure.browser.event :as event]
            [goog.string.format :as gformat]
            [goog.string :as gstring]
            [oyster.dom :as dom :refer [length item as-seq
                                        set-html! set-style! by-id by-class
                                        by-tag html update-html! update-height!]]
            [oyster.view]
            [oyster.async :as async :refer [listen every process-channel tap-chan]]
            [oyster.map :as m])
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]]))

(defn log [x] (.log js/console x))
(log "Oyster Cloyster.")

(defn seed [] 1234)

(defn hovers
  "A channel of tile <spans> which have been hovered over"
  []
  (->> (listen (by-id :map) :mouseover)
       (map< #(.-target %))))

(defn show-selected
  [hover-chan]
  "Watch a hover channel, keeping the .selected class on the current hovered element"
  (go
   (loop [last nil]
     (let [next (<! hover-chan)]
       (dom/add-class next "selected")
       (if last
         (dom/remove-class last "selected"))
       (recur next)))))

(defn selected-tiles
  "A channel populated with selected map tiles.
  (Tiles are selected by hovering.)"
  [hover-chan]
  (->> hover-chan
       (remove< #(nil? (.-tile (.-attributes %))))
       (map< #(.-value (.-tile (.-attributes %))))))

(defn commands
  []
  (->> (listen js/document :keypress)
       (map< #(.-keyCode %))))

;; render page
(defn game []
  (let [m (m/empty-map (seed))]
    (set-html! (by-id :content) (.-outerHTML (oyster.view/main-game m)))
    (let [hover-chan (mult (hovers))
          tiles (selected-tiles (tap-chan hover-chan))
          cmds (commands)]
      (show-selected (tap-chan hover-chan))
      (process-channel log tiles)
      (process-channel log cmds))))

;; begin the game when everything is loaded
(set! (.-onload js/window) game)
