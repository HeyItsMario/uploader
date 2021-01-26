(ns uploader.handler
  (:require
    [compojure.core :refer [GET routes defroutes]]
    [compojure.route :refer [resources not-found]]
    [ring.adapter.jetty :refer [run-jetty]]
    [ring.util.response :refer [resource-response]]
    [ring.middleware.params :refer [wrap-params]]
    [uploader.routes.records :refer [record-routes]]
    ))


(def app
  (->> (apply routes [record-routes
                      (not-found "Page not found")])
       (wrap-params)))

(defn start-server [& args]
  (run-jetty app {:port 8080 :join? false})
  (println "listening on port :8080"))
