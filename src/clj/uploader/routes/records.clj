(ns uploader.routes.records
  (:require [cheshire.core :refer [encode]]
            [compojure.core :refer [ANY context routes]]
            [liberator.representation :refer [ring-response]]
            [liberator.core :refer [defresource]]
            [taoensso.timbre :refer [info  warn  error  fatal set-level!]]
            [uploader.datastore.db :refer [get-entities save-entity!]]
            [uploader.entities.record :refer [sorted-records]]
            [uploader.parser.file-parser :refer [format-values]]
            ))


(defn get-sorted-records [sort-type]
  (case sort-type
    "/gender"    (sorted-records :gender)
    "/birthdate" (sorted-records :birthdate)
    "/name"      (sorted-records :last-name)
    (sorted-records :unkown)))


(defresource records 
  :post! (fn [_])
  :handle-ok (fn [ctx]
               (let [sort-type (-> ctx :request :path-info)
                     records   (get-sorted-records sort-type)]
                 (ring-response {:body (encode (map format-values records))})))
  :handle-exception (fn [ctx]
                      (println "BAD ERROR")
                      (println (.getMessage (:exception ctx))))
  :available-media-types ["application/json"]
  :allowed-methods [:get :post])



(def record-routes
  (context "/records" []
           (ANY "/" [] records)
           (ANY "/gender" [] records)
           (ANY "/birthdate" [] records)
           (ANY "/name" [] records)))
