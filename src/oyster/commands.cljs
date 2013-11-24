(ns oyster.commands
  (:require [dommy.core :as dommy]
            [oyster.map :as m])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def all-commands
  {:t {:name :till
       :description "Till the soil."
       :action (fn [t] (if (= :grass @(:type t)) (m/change-tile t :tilled-soil)))}})
