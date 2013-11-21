(ns oyster.view
  (:use-macros [dommy.macros :only [node deftemplate]])
  (:require [dommy.core :as dommy]
            [dommy.attrs :as attrs]
            [oyster.tiles :as t]
            [oyster.intro :as intro]
            [oyster.async :as async]
            [oyster.utilities :as util :refer [log]]
            [cljs.core.async :refer [chan]])
  (:require-macros [cljs.core.async.macros :as m :refer [go go-loop alt!]]
                   [dommy.macros :as dm :refer [node]]))

(defn status-bar []
  [:div#status-bar])

(defn map-cell [[r c] x]
  (let [tile (t/all-tiles x)]
    (node [:span {:style (attrs/style-str (:style tile)) :tile [r c]}
           (:char tile)])))

(defn map-row [ts]
  [:div.map-row ts])

(defn draw-map [tss]
  [:div#map
   (map map-row tss)])

(deftemplate main-game [map-el]
  [:div
   (status-bar)
   map-el
   [:div#oyster
    (intro/oyster 6)
    [:div#title "Oyster Cloister."]]])

(defn change-tile
  [el tile-key]
  (let [tile (t/all-tiles tile-key)]
    (-> el
        (dommy/set-attr! :style (attrs/style-str (:style tile)))
        (dommy/set-html! (:char tile)))))

(defn binding-chan
  [el]
  (let [c (chan)]
    (async/process-channel (partial change-tile el) c)
    c))
