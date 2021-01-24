(ns uploader.parser.file-parser
  (:require [clj-time.format :as tf]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :refer [print-table]]
            [uploader.entities.record :refer [upload-records! sorted-records]]))

(defn parse-file
  [path]
  (let [contents (slurp path)
        records  (str/split contents #"\n")]
    (upload-records! records)))

(defn files-exists? [args]
  (= (count (filter (fn [file-path] (.exists (io/file file-path))) args))
     (count args)))

(defn format-values [record]
  (update record :birthdate #(tf/unparse (tf/formatter "M/d/YYYY") %)))

(defn sorted-output [sort-type]
  (let [records (sorted-records sort-type)]
    (print-table [:last-name :first-name :gender :favorite-color :birthdate]
                                (map format-values records))))

(defn -main [& args]
  (if (not (files-exists? args))
    (println "Invalid file paths. Make sure you are typing the correct file path. Exiting...")
    (do (println "Uploading..")
        (doseq [fp args]
          (parse-file fp))
        (doseq [t [:gender :birthdate :last-name]]
          (print "Table sorted by " (name t))
          (sorted-output t)))))
