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
            [oyster.commands :as c]
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
  (map< #(.-target %)
    (listen (by-id :map) :mouseover)))

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
       (map< keyword)
       (filter< (partial contains? c/all-commands))
       (map< c/all-commands)))

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

(defn execute-player-commands
  [m cmds status-el]
  (letfn [(execute [[cmd idx]]
            (if-let [t (get-in m idx)]
              (do
                  (log (str (:description cmd) " on " (name @(:type t))))
                  (when ((:usable-on cmd) @(:type t))
                    ((:action cmd) t)
                    (dommy.core/set-html! status-el (:description cmd))))))]
    (process-channel execute cmds)))

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
    (let [{map-el :el tiles :tiles} (m/random-tile-map (seed))]
      (dommy.core/replace-contents! (by-id :content) (oyster.view/main-game map-el))
      (let [hover-chan (mult (hovers))
            cmds (tile-commands
                   (selected-tiles (tap-chan hover-chan))
                   (commands))]
        (show-selected (tap-chan hover-chan))
        (show-status tiles (by-id :status-bar) (selected-tiles (tap-chan hover-chan)))
        (execute-player-commands tiles cmds (by-id :status-bar))))))

;; begin the game when everything is loaded
(set! (.-onload js/window) game)
(log "Oyster Cloister.")
