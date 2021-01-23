(ns uploader.datastore.db
  (:require [clj-time.core :as t]))


(defonce db (atom {}))

(defn- uuid [] (.toString (java.util.UUID/randomUUID)))

(defn- view-db [] @db)

(defn- reset-db! [] (reset! db {}))

(defn save-entity! [{:keys [type data]}]
  (swap! db #(update % type merge {(uuid) data}))
  data)

(defn get-entities [type]
  (get @db type))

