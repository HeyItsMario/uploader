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

(def sample-records
  ["Smith jane female violet 01-06-1930"
   "uncle|sam | male| purple |01-06-1980"
   "wonderland,alice,female,blue,01-03-1945"
   "Smith joe male blue 01-06-1950"
   "enaj jane female pink 01-06-1945"
   "public joe male red 01-06-1970"
   ]
  )

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
             (= 36 (count (first ids))))))

  ;; teardown db
  (reset-db!)

  )


(deftest sorted-records-test
  (let [ids            (er/upload-records! sample-records)
        bday-records   (vec (er/sorted-records :birthdate))
        gender-records (vec (er/sorted-records :gender))
        name-records   (vec (er/sorted-records :last-name))
        ]

    ;; Testing bday-order
    (is (and (and (= (:last-name (get bday-records 5)) "Uncle")
                  (= (:last-name (get bday-records 4)) "Public")
                  (= (:last-name (get bday-records 3)) "Smith")
                  (= (:last-name (get bday-records 2)) "Enaj")
                  (= (:last-name (get bday-records 1)) "Wonderland")
                  (= (:last-name (get bday-records 0)) "Smith"))

             ;; Testing gender order
             (and (= (:last-name (get gender-records 5)) "Uncle")
                  (= (:last-name (get gender-records 4)) "Smith")
                  (= (:last-name (get gender-records 3)) "Public")
                  (= (:last-name (get gender-records 2)) "Wonderland")
                  (= (:last-name (get gender-records 1)) "Smith")
                  (= (:last-name (get gender-records 0)) "Enaj"))

             ;; Testing name order
             (and (= (:last-name (get name-records 5)) "Enaj")
                  (= (:last-name (get name-records 4)) "Public")
                  (= (:last-name (get name-records 3)) "Smith")
                  (= (:last-name (get name-records 2)) "Smith")
                  (= (:last-name (get name-records 1)) "Uncle")
                  (= (:last-name (get name-records 0)) "Wonderland"))
             ))

    )

  ;; teardown db
  (reset-db!)
  )


(comment


  (er/normalize-values '("smith" "joe" "male" "blue" "01-01-1970"))

  (er/filter-invalid-records invalid-records)

  (er/upload-records! [pipe-example space-example comma-example])

  (er/upload-records! sample-records)

  (view-db)

  (reset-db!)

  (run-tests)



  )
