(ns uploader.repl
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.file :refer [wrap-file]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [taoensso.timbre :refer [info  warn  error  fatal]]
            [uploader.handler :refer [app init]]))

(defonce server (atom nil))

(defn handler []
  (-> #'app
      (wrap-file "resources")
      (wrap-file-info)))

(defn start!
  "Used for starting the server in dev mode from the REPL"
  []
  (init)
  (reset! server
          (run-jetty (handler) {:port 8080
                                :open-browser? false
                                :join? false}))
  (println "You can view this on http://localhost:8080"))

(defn stop! []
  (info "Shutting down...")
  (.stop @server)
  (reset! server nil))




