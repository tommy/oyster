(ns oyster.view
  (:use-macros [dommy.macros :only [node deftemplate]])
  (:require [dommy.core :as dommy]
            [oyster.tiles :as t]
            [oyster.intro :as intro]))


(defn map-cell [r c x]
  [:span {:class (name x) :tile [r c]} (:char (t/all-tiles x))])

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
