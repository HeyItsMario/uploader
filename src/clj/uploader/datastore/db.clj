(ns uploader.datastore.db
  (:require [clj-time.core :as t]))


(defonce db (atom {}))

(defn uuid [] (.toString (java.util.UUID/randomUUID)))

(defn view-db [] @db)

(defn reset-db! [] (reset! db {}))

(defn save-entity! [{:keys [type data]}]
  (let [id (uuid)]
    (swap! db #(update % type merge {id data}))
    id))

(defn get-entities [type]
  (get @db type))

(defn get-record-by-id [id]
  (get-in @db [:records id]))
