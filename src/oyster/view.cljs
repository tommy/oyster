(ns oyster.view
  (:use-macros [crate.def-macros :only [defpartial defelem]])
  (:require [crate.core :as crate]
            [oyster.tiles :as t]))

(defpartial oyster-body []
  [:body
   [:div.resources
    [:p
     [:span#oysters "0"]]
    [:button#eat-button "Eat all the oysters."]
    [:button#throw-button "Release 10 oysters."]]
   [:div.hunger-container
    [:div#hunger.hunger-bar]]])

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

(defpartial main-game [m]
  [:div
   (status-bar)
   (draw-map m)])
