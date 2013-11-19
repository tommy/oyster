(ns oyster.view
  (:use-macros [dommy.macros :only [node deftemplate]])
  (:require [dommy.core :as dommy]
            [dommy.attrs :as attrs]
            [oyster.tiles :as t]
            [oyster.intro :as intro]))

(defn map-cell [r c x]
  (let [tile (t/all-tiles x)]
    [:span {:style (attrs/style-str (:style tile)) :tile [r c]}
     (:char tile)]))

(defn map-row [r xs]
  [:div.map-row
   (map-indexed (partial map-cell r) xs)])

(defn draw-map [xss]
  [:div#map
   (map-indexed map-row xss)])

(defn status-bar []
  [:div#status-bar])

(declare oyster)

(deftemplate main-game [m]
  [:div
   (status-bar)
   (draw-map m)
   [:div#oyster
    (intro/oyster 6)
    [:div#title "Oyster Cloister."]]])
