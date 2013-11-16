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

(defpartial map-cell [x]
  (case x
    \~ [:span.water {:tile [0,0]} x]
    \, [:span.grass {:tile [0,0]} x]
    \. [:span.beach {:tile [0,0]} x]
    [:span {:tile [0,0]} x]))

(defpartial map-row [xs]
  [:div.map-row
   (map map-cell xs)])

(defpartial draw-map [xss]
  [:div#map
   (map map-row xss)])
