(ns oyster.view
  (:use-macros [crate.def-macros :only [defpartial]])
  (:require [crate.core :as crate]))

(defpartial oyster-body []
  [:body
   [:div.resources
    [:p
     [:span#oysters "0"]]
    [:button#eat-button "Eat all the oysters."]
    [:button#throw-button "Release 10 oysters."]]
   [:div.hunger-container
    [:div#hunger.hunger-bar]]])

(defpartial map-cell [r c x]
  (case x
    \~ [:span.water {:tile [r,c]} x]
    \, [:span.grass {:tile [r,c]} x]
    \. [:span.beach {:tile [r,c]} x]
    [:span {:tile [r,c]} x]))

(defpartial map-row [r xs]
  [:div.map-row
   (map-indexed (partial map-cell r) xs)])

(defpartial draw-map [xss]
  [:div#map
   (map-indexed map-row xss)])

(defpartial main-game [m]
  [:p
   (draw-map m)])
