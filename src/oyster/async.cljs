(ns oyster.async
  (:use [cljs.core.async :only [put! <!! <! chan timeout tap]])
  (:require [clojure.browser.event :as event])
  (:require-macros [cljs.core.async.macros :as m :refer [go go-loop alt!]]))

;; creating channels
  
(defn listen
  "Create a channel of events on an element
  of a given type."
  [el type]
  (let [c (chan)
        f #(put! c %)]
    (if (= el js/document)
      (.addEventListener el (name type) f)
      (event/listen el type f))
    c))

(defn every
  "Execute f every interval ms, and put its return
  value to the returned channel, AS LONG AS VALUES
  ARE BEING READ FROM THE CHANNEL."
  [interval f]
  (let [rc (chan)]
    (go-loop []
             (<! (timeout interval))
             (>! rc (f))
             (recur))
    rc))

(defn process-channel
  "Process all messages on channel c with function f,
  until the channel is closed."
  [f c]
  (go-loop []
           (if-let [x (<! c)]
             (do (f x) (recur)))))

(defn bind
  "Register f as an event handler for events on el."
  [el type f]
  (let [c (listen el type)]
    (process-channel f c)))

(defn tap-chan
  "Create new channel and register it as a tap on a mult(iple) channel. Return the new tap."
  ([mult]
   (tap-chan mult nil))
  ([mult buf-or-n]
   (let [c (chan buf-or-n)]
     (tap mult c)
     c)))
