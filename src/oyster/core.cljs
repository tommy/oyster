(ns oyster.core
  (:use [cljs.core.async :only [chan timeout map< filter< remove< mult close!]])
  (:require [clojure.browser.event :as event]
            [goog.string.format :as gformat]
            [goog.string :as gstring]
            [dommy.attrs]
            [dommy.core]
            [oyster.dom :as dom :refer [by-id]]
            [oyster.view]
            [oyster.async :as async :refer [listen
                                            every
                                            process-channel
                                            process-with-prev
                                            tap-chan]]
            [oyster.map :as m]
            [oyster.intro :as intro]
            [oyster.utilities :as util :refer [log]])
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]]))

(defn seed
  "Get the seed from the query string, if any,
  otherwise generate a random one."
  []
  (if-let [v (util/query-value :seed)]
    v
    (.random js/Math)))

(def parse-coords
  (comp
    (partial map js/parseInt)
    next
    (partial re-matches #"\[(\d+) (\d+)\]")))

(defn hovers
  "A channel of tile <spans> which have been hovered over"
  []
  (->> (listen (by-id :map) :mouseover)
       (map< #(.-target %))))

(defn show-selected
  "Watch a hover channel, keeping the .selected class on the current hovered element"
  [hover-chan]
  (let [change-class (fn [prev next]
                       (dommy.attrs/add-class! next :selected)
                       (if prev (dommy.attrs/remove-class! prev :selected)))]
    (process-with-prev change-class hover-chan)))

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
  (let [f (fn [t] (dommy.core/set-html! el (m/tile-description m t)))]
    (process-channel f tiles)))

(defn game
  "Start the game."
  []
  (go
    (if (util/query-value :intro)
      (<! (intro/animate! (map intro/oyster (range 6 11 2)) 2)))
    (let [m (m/empty-map (seed))]
      (dommy.core/replace-contents! (by-id :content) (oyster.view/main-game m))
      (let [hover-chan (mult (hovers))
            cmds (tile-commands
                   (selected-tiles (tap-chan hover-chan))
                   (commands))]
        (show-selected (tap-chan hover-chan))
        (show-status m (by-id :status-bar) (selected-tiles (tap-chan hover-chan)))
        (process-channel (comp log clj->js) cmds)))))

;; begin the game when everything is loaded
(set! (.-onload js/window) game)
(log "Oyster Cloister.")
