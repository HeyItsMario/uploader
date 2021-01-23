(ns uploader.handler
  (:require
    [compojure.core :refer [GET routes defroutes]]
    [compojure.route :refer [resources not-found]]
    [ring.adapter.jetty :refer [run-jetty]]
    [ring.util.response :refer [resource-response]]
    [taoensso.timbre :refer [info  warn  error  fatal set-level!]]
    [uploader.routes.records :refer [record-routes]]
    ))


(defn init []
  (set-level! :debug)
  (info "starting up..."))

(def app
  (apply routes [record-routes
                 (not-found "Page not found")]))

(defn start-server [& args]
  (run-jetty app {:port 8080 :join? false}))
