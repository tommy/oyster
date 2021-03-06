(ns oyster.server
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.resource :as resources]
            [ring.middleware.content-type :as content-type]
            [ring.util.response :as response])
  (:gen-class))

(def no-content
  {:status 404})

(defn handler [request]
  (case (:uri request)
    "/" (response/redirect "/index.html")
    no-content))

(def app
  (-> handler
      (resources/wrap-resource "public")
      content-type/wrap-content-type))

(defn -main [& args]
  (jetty/run-jetty app {:port 3000}))
