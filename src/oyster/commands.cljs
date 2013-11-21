(ns oyster.commands
  (:require [dommy.core :as dommy])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def all-commands
  {:t {:name :till
       :description "Till the soil."
       :action (fn [t]
                 (let [c (:binding-chan t)]
                   (go (>! c :tilled-soil))))}})