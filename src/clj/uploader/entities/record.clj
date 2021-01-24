(ns uploader.entities.record
  (:require [clj-time.core :as t]
            [clj-time.coerce :as tc]
            [clj-time.format :as tf]
            [clojure.string :as str]
            [uploader.datastore.db :refer [get-entities reset-db!]]
            [uploader.datastore.db :refer [save-entity!]]))


;; NOTE: Functions below for uploading records, eg. normalizing data, transformations, etc.

(def multi-parser
  (tf/formatter (t/default-time-zone) "YYYY/MM/dd"  "YYYY-MM-dd" "MM/dd/YYYY" "MM-dd-YYYY"))

(defn delimiter-type [record-str]
  (let [chars (set record-str)]
    (cond
      (contains? chars \|) "\\|"
      (contains? chars \,) ","
      :else                "\\s+")))

(defn split-record-entry
  "Takes in a record string and returns a vector consisting of record parts."
  [rs]
  (let [delimiter (delimiter-type rs)
        regex     (re-pattern delimiter)]
    (map str/trim (str/split rs regex))))

(defn normalize-values
  "Take a record, [fname lname gender color dob], and capitalizes all values."
  [record]
  (map str/upper-case record))

(defn filter-invalid-records
  "Takes a collection of records and filters out any with less than 5 parts"
  [records]
  (filter (comp (partial = 5) count) records))

(defn transform
  "Takes in a record and applies transformations."
  [record]
  (-> record
      (update :birthdate #(tf/parse multi-parser %))
      (update :first-name str/capitalize)
      (update :last-name str/capitalize)
      (update :gender str/capitalize)
      (update :favorite-color str/capitalize)
      ))

(defn create-record [data]
  (->> (map (comp vec list) [:last-name :first-name :gender :favorite-color :birthdate] data)
       (into {})
       (transform)))

(defn create-record-entity [record]
  {:type :records
   :data record})

(defn upload-records!
  "Takes a collection of raw records, cleans and uploads to the datastore.
   Returns a list of UUID's corresponding to each record's ID in the DB."
  [records]
  (let [normalized-records (map (comp normalize-values split-record-entry str/trim) records)]
    (doall (->> normalized-records
                (filter-invalid-records)
                (map (comp save-entity! create-record-entity create-record))))))

(defn upload-record! [])


;; NOTE: Functions below for working with records.

(defn sort-records
  ([f]
   (sort-by f (vals (get-entities :records))))
  ([f c]
   (sort-by f c (vals (get-entities :records)))))

(defn sorted-records [sort-type]
  (case sort-type
    :birthdate (sort-records :birthdate)
    :last-name (sort-records :last-name #(compare %2 %1))
    :gender    (sort-records (juxt :gender :last-name))
    (vals (get-entities :records))))

