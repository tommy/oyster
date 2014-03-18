(ns oyster.commands
  (:require [dommy.core :as dommy]
            [oyster.map :as m]
            [oyster.async :as a])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def all-commands
  {:t {:name :till
       :description "Tilled the soil."
       :usable-on #{:grass}
       :action (fn [t] (m/change-tile t :tilled-soil))}
   :p {:name :plant
       :description "Planted a radish."
       :usable-on #{:tilled-soil}
       :action (fn [t]
                 (m/change-tile t :radish-seed)
                 (a/after 5000 #(m/change-tile t :radish)))}
   :h {:name :harvest
       :description "Harvested a radish."
       :usable-on #{:radish}
       :action (fn [t] (m/change-tile t :tilled-soil))}})
