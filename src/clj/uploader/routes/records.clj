(ns uploader.routes.records
  (:require [compojure.core :refer [ANY context routes]]
            [liberator.representation :refer [ring-response]]
            [liberator.core :refer [defresource]]
            [taoensso.timbre :refer [info  warn  error  fatal set-level!]]
            ))


(defn sorted-records [sort-type]
  (case sort-type
    :gender "gender"
    :dob "dob"
    :name "name"
    "unknown"))


(defresource records [sort-type]
  :post! (fn [_])
  :handle-ok (fn [ctx]
               (info {:event "get-sorted-records"
                      :type sort-type})
               (let [records (sorted-records sort-type)]
                 (ring-response {:body records})))
  :handle-exception (fn [ctx]
                      (println (.getMessage (:exception ctx))))
  :available-media-types ["application/json"]
  :allowed-methods [:get :post])



(def record-routes
  (context "/records" []
           (ANY "/" [] (records nil))
           (ANY "/gender" [] (records :gender))
           (ANY "/birthdate" [] (records :dob))
           (ANY "/name" [] (records :name))))
