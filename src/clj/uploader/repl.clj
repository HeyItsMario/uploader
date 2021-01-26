(ns uploader.repl
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.file :refer [wrap-file]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [uploader.handler :refer [app]]))

(defonce server (atom nil))

(defn handler []
  (-> #'app
      (wrap-file "resources")
      (wrap-file-info)))

(defn start!
  "Used for starting the server in dev mode from the REPL"
  []
  (reset! server
          (run-jetty (handler) {:port 8080
                                :open-browser? false
                                :join? false}))
  (println "Listening on port 8080"))

(defn stop! []
  (println "Shutting down...")
  (.stop @server)
  (reset! server nil))




