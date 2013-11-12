(ns oyster.view
  (:use-macros [crate.def-macros :only [defpartial]])
  (:require [crate.core :as crate]))

(defpartial main-body []
  [:body
   [:div.resources
    [:p
     [:span#oysters "0"]]
    [:button#eat-button "Eat all the oysters."]
    [:button#throw-button "Release 10 oysters."]]
   [:div.hunger-container
    [:div#hunger.hunger-bar]]])
