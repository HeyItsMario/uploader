(ns uploader.routes.records
  (:require [cheshire.core :refer [encode decode]]
            [compojure.core :refer [ANY context routes]]
            [liberator.representation :refer [ring-response]]
            [liberator.core :refer [defresource]]
            [uploader.datastore.db :refer [reset-db! get-entities save-entity!]]
            [uploader.entities.record :refer [sorted-records upload-records!]]
            [uploader.parser.file-parser :refer [format-values]]

            [clojure.string :as str]))


(defn get-sorted-records
  "Takes in the file-path and retrieves sorted records based on the type.
   If no file path matches, known ones, returns records without any sort."
  [sort-type]
  (case sort-type
    "/gender"    (sorted-records :gender)
    "/birthdate" (sorted-records :birthdate)
    "/name"      (sorted-records :last-name)
    (sorted-records :unkown)))


(defn upload-json-record [{:keys [first-name last-name gender favorite-color birthdate]}]
  (first (upload-records! [(str/join " " [last-name first-name gender favorite-color birthdate])]))
  )

(defn upload-text-record [record]
  ;; NOTE: Just in case anybody tries to get sneaky... Not the best way to handle this but should suffice for now.
  (first (upload-records! [(first (str/split record #"\n"))])))

(defn record-processable? [content-type]
  (case content-type
    "application/json"         [true {::type :json}]
    "text/plain;charset=UTF-8" [true {::type :utf}]
    "text/plain"               [true {::type :text}]
    false))

(defn post-record! [ctx]
  (case (::type ctx)
    :json (upload-json-record (-> ctx :request :body slurp (decode true)))
    :utf  (upload-text-record (-> ctx :request :body slurp))
    :text (upload-text-record (-> ctx :request :body slurp))))

(defresource records 
  :post! (fn [ctx]
           (let [result (post-record! ctx)]
             {::uuid result}))
  :handle-created (fn [ctx]
                    (let [id       (::uuid ctx)
                          success? (not (nil? id))]
                      (ring-response {:body (encode {:success? success?
                                                     :id       (if success? id 0)
                                                     :error    (if success?
                                                                 ""
                                                                 "Could not upload your entry. Please make sure you are uploading in the correct format.")})})
                      ))
  :processable? (fn [ctx]
                  (case (-> ctx :request :request-method)
                    :post (record-processable? (-> ctx :request :content-type))
                    true))

  :handle-unprocessable-entity (fn [ctx]
                                 (ring-response {:body
                                                 (encode {:error (str "Can't process media-type '" (-> ctx :request :content-type) "'. Please use 'text/plain' or 'application/json'.")})}))
  :handle-ok (fn [ctx]
               (let [sort-type (-> ctx :request :path-info)
                     records   (get-sorted-records sort-type)]
                 (ring-response {:body (encode (map format-values records))})))

  :handle-exception (fn [ctx]
                      (println (.getMessage (:exception ctx))))
  :available-media-types ["application/json"]
  :allowed-methods [:get :post])


(def record-routes
  (context "/records" []
           (ANY "/" [] records)
           (ANY "/gender" [] records)
           (ANY "/birthdate" [] records)
           (ANY "/name" [] records)))

(comment

  (reset-db!)

  (get-entities :records)

  )
