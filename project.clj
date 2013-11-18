(defproject oyster "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2030"]
                 [org.clojure/core.async "0.1.242.0-44b1e3-alpha"]
                 [prismatic/dommy "0.1.1"]
                 [ring "1.1.8"]
                 ]

  :plugins [[lein-cljsbuild "1.0.0-alpha2"]
            [lein-ring "0.8.3"]]

  :hooks [leiningen.cljsbuild]

  :source-paths ["src"]

  :cljsbuild { 
    :builds [{:id "oyster"
              :source-paths ["src"]
              :compiler {
                :output-to "resources/public/js/oyster.js"
                :output-dir "resources/public/js"
                :optimizations :none
                :source-map "oyster.js.map"}}]}
  :ring {:handler oyster.server/app})
