(ns uploader.entities.record-test
  (:require [uploader.entities.record :as er]
            [uploader.datastore.db :refer [view-db reset-db!]]
            [clojure.test :refer [deftest is run-tests]]))


(def pipe-example "smith | joe|male | blue | 01-01-1970")

(def comma-example "smith,joe,male,blue , 01-01-1970")

(def space-example "smith joe   male blue 01-01-1970")

(def invalid-records
  ['("SMITH" "JOE" "MALE" "01-01-1970") ;; Missing data
   '("SMITH" "JOE" "M" "BLUE" "01-01-1970") ;; Invalid gender format.
   '("SMITH" "JANE" "F" "BLUE" "01-01-1970") ;; Invalid gender format.
   '("SMITH" "JANE" "FEMLE" "BLUE" "01-01-1970") ;; Invalid gender spelling.
   '("SMITH" "JOE" "MLE" "BLUE" "01-01-1970") ;; Invalid gender spelling.
   '("SMITH" "JANE" "CIRCLE" "BLUE" "01-01-1970") ;; Invalid gender.
   ])

(deftest delimiter-type-test
  (is (and (= "\\|" (er/delimiter-type pipe-example))
           (= "," (er/delimiter-type comma-example))
           (= "\\s+" (er/delimiter-type space-example)))))

(deftest split-record-entry-test
  (is (and (= (er/split-record-entry pipe-example)
              '("smith" "joe" "male" "blue" "01-01-1970"))
           (= (er/split-record-entry comma-example)
              '("smith" "joe" "male" "blue" "01-01-1970"))
           (= (er/split-record-entry space-example)
              '("smith" "joe" "male" "blue" "01-01-1970"))
           )))

(deftest normalize-values-test
  (is (= (er/normalize-values '("smith" "joe" "male" "blue" "01-01-1970"))
         '("SMITH" "JOE" "MALE" "BLUE" "01-01-1970"))))

(deftest filter-invalid-records-test
  (is (and (= true (empty? (er/filter-invalid-records invalid-records)))
           (= 1 (count (er/filter-invalid-records [["JOE" "SMITH" "MALE" "BLUE" "01-01-1970"]])))))
  )

(deftest create-record-test
  (let [record (er/create-record ["JOE" "SMITH" "MALE" "BLUE" "01-01-1970"])]
    (is (and (= true (every? (partial contains? #{:last-name :first-name :gender :favorite-color :birthdate})
                             (keys record)))
             (= org.joda.time.DateTime (type (:birthdate record)))))))


(deftest create-record-entity-test
  (let [record (er/create-record ["JOE" "SMITH" "MALE" "BLUE" "01-01-1970"])
        entity (er/create-record-entity record)]
    (and (= true (every? (partial contains? #{:type :data})
                         (keys entity)))
         (= :records (:type entity)))
    ))

(deftest upload-records-tests
  (let [ids (er/upload-records! [pipe-example space-example comma-example])]
    (is (and (= 3 (count ids))
             (= 36 (count (first ids)))))))


(comment


  (er/normalize-values '("smith" "joe" "male" "blue" "01-01-1970"))

  (er/filter-invalid-records invalid-records)

  (er/upload-records! [pipe-example space-example comma-example])

  (view-db)

  (reset-db!)

  (run-tests)


  )
