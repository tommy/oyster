(ns oyster.commands
  (:require [dommy.core :as dommy]))

(def all-commands
  {:t {:name :till
       :description "Till the soil."
       :action (fn [t] (dommy/set-html! (:el t) \=))}})
