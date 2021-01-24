(defproject uploader "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [com.taoensso/timbre "5.1.0"]
                 [cheshire "5.10.0"]
                 [clj-time "0.15.2"]
                 [liberator "0.15.3"]
                 [compojure "1.6.2"]
                 [yogthos/config "1.1.7"]
                 [ring "1.8.2"]]

  :plugins [[lein-shadow "0.3.1"]
            [lein-shell "0.5.0"]]

  :min-lein-version "2.9.0"

  :source-paths ["src/clj"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "1.0.2"]]
    :source-paths ["dev"]}

   :prod {}
   :uberjar {:source-paths ["env/prod/clj"]
             :omit-source  true
             :main         uploader.server
             :aot          [uploader.server]
             :uberjar-name "uploader.jar"
             :prep-tasks   ["compile" ["release"]]}}

  :prep-tasks [])
