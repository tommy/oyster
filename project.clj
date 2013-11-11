(defproject oyster "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2030"]]

  :plugins [[lein-cljsbuild "1.0.0-alpha2"]]

  :source-paths ["src"]

  :cljsbuild { 
    :builds [{:id "oyster"
              :source-paths ["src"]
              :compiler {
                :output-to "oyster.js"
                :output-dir "out"
                :optimizations :none
                :source-map true}}]})
