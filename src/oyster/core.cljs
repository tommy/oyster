(ns oyster.core
  (:use [cljs.core.async :only [put! <!! <! chan timeout map< filter< remove< mult close!]])
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

(defn seed [] (.random js/Math))

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
      (when-let [next (<! hover-chan)]
        (dom/add-class next "selected")
        (if last
          (dom/remove-class last "selected"))
        (recur next)))))

(defn parse-coords
  [s]
  (map js/parseInt (next (re-matches #"\[(\d+) (\d+)\]" s))))

(defn selected-tiles
  "A channel populated with selected map tiles.
  (Tiles are selected by hovering.)"
  [hover-chan]
  (->> hover-chan
       (remove< #(nil? (.-tile (.-attributes %))))
       (map< #(.-value (.-tile (.-attributes %))))
       (map< parse-coords)))

(defn commands
  "A channel populated with the player's key presses."
  []
  (->> (listen js/document :keypress)
       (map< #(.-charCode %))
       (map< #(.fromCharCode js/String %))
       (map< keyword)))

(defn tile-commands
  "Combine a channel of selected-tiles and a channel of keyboard commands
  into a channel of commands at tile locations."
  [selected-tiles commands]
  (let [c (chan)]
    (go (loop [last nil]
          (if-let [next (alt!
                          selected-tiles ([tile] tile)
                          commands ([cmd] (do (>! c [cmd last]) last)))]
            (recur next)
            (close! c))))
    c))

(defn show-status
  "Replace the contents of el with a description of the most recently
  selected tile, read from the tiles channel."
  [m el tiles]
  (let [f (fn [t] (set-html! el (m/tile-description m t)))]
    (process-channel f tiles)))

(defn game
  "Start the game."
  []
  (let [m (m/empty-map (seed))]
    (set-html! (by-id :content) (.-innerHTML (oyster.view/main-game m)))
    (let [hover-chan (mult (hovers))
          cmds (tile-commands
                 (selected-tiles (tap-chan hover-chan))
                 (commands))]
      (show-selected (tap-chan hover-chan))
      (show-status m (by-id :status-bar) (selected-tiles (tap-chan hover-chan)))
      (process-channel (comp log clj->js) cmds))))

;; begin the game when everything is loaded
(set! (.-onload js/window) game)
(log "Oyster Cloister.")
