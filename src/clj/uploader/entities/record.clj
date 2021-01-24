(ns uploader.entities.record
  (:require [clojure.string :as str]
            [uploader.datastore.db :refer [save-entity!]]))

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

(defn create-record [data]
  (->> (map (comp vec list) [:last-name :first-name :gender :favorite-color :birthdate] data)
       (into {})))

(defn create-record-entity [record]
  {:type :records
   :data record})

(defn upload-records!
  "Takes a collection of raw records, cleans and uploads to the datastore.
   Returns a list of UUID's corresponding to each record's ID in the DB."
  [records]
  (let [normalized-records (map (comp normalize-values split-record-entry str/trim) records)]
    (->> normalized-records
         (filter-invalid-records)
         (map (comp save-entity! create-record-entity create-record)))))

(defn upload-record! [])


