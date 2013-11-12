(ns oyster.async
  (:use [cljs.core.async :only [put! <!! <! chan timeout]])
  (:require [clojure.browser.event :as event])
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]]))

;; creating channels
  
(defn listen
  "Create a channel of events on an element
  of a given type."
  [el type]
  (let [c (chan)]
    (event/listen el type #(put! c %))
    c))

(defn every
  "Execute f every interval ms, and put its return
  value to the returned channel, AS LONG AS VALUES
  ARE BEING READ FROM THE CHANNEL."
  [interval f]
  (let [rc (chan)]
    (go
      (loop []
        (<! (timeout interval))
        (>! rc (f))
        (recur)))
    rc))

(defn process-channel
  "Process all messages on channel c with function f,
  until the channel is closed."
  [c f]
  (go (loop []
        (if-let [x (<! c)]
          (do (f x) (recur))))))

(defn bind
  "Register f as an event handler for events on el."
  [el type f]
  (let [c (listen el type)]
    (process-channel c f)))
