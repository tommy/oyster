(ns oyster.view
  (:use-macros [dommy.macros :only [node deftemplate]])
  (:require [dommy.core :as dommy]
            [dommy.attrs :as attrs]
            [oyster.tiles :as t]
            [oyster.intro :as intro]))

(defn status-bar []
  [:div#status-bar])

(defn map-cell [r c x]
  (let [tile (t/all-tiles x)]
    [:span {:style (attrs/style-str (:style tile)) :tile [r c]}
     (:char tile)]))

(defn map-row [ts]
  [:div.map-row
   (map :el ts)])

(defn draw-map [tss]
  [:div#map
   (map map-row tss)])

(deftemplate main-game [m]
  [:div
   (status-bar)
   (draw-map m)
   [:div#oyster
    (intro/oyster 6)
    [:div#title "Oyster Cloister."]]])
