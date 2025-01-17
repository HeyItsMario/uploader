(ns uploader.datastore.db-test
  (:require [uploader.datastore.db :as db]
            [clojure.test :refer [deftest is run-tests]]))

;; NOTE: Helper functions to generate some test data.
(defn- random [coll]
  (rand-nth coll))

(defn- create-record []
  {:first-name     (random ["bob" "Alice" "joe" "Mike" "WiLiam"])
   :last-name      (random ["smith" "De'LONG" "roberts" "WINTERS" "Sommers"])
   :birthdate      (random ["01/06/1993" "2/4/1967" "10/2/2001" "2012/9/31"
                            "01-06-1993" "2-4-1967" "10-2-2001" "2012-9-31"]) ;; assumption that dates will becoming in mm/dd/yyyy, mm-dd-yyyy, yyyy/mm/dd
   :favorite-color (random ["blue" "Red" "PURPLE" "BlAcK" "yELLOw"])
   :gender         (random ["Female" "male" "f" "m" "F" "m" "MALE"])})




;; NOTE: implicitly tests both saving/getting records.
(deftest save-entity-test
  (let [record (create-record)
        entity {:type :records :data record}
        id     (db/save-entity! entity)]
    (is (= record  (db/get-record-by-id id))))

  ;; Teardown db.
  (db/reset-db!)
  )

(deftest view-db-test
  (is (= true (not (nil? (db/view-db))))))

(deftest get-entities-test
  ;; NOTE: creating and save ten records
  (doseq [_ (range 10)]
    (db/save-entity! {:type :records :data (create-record)}))

  ;; NOTE: `(db/get-entities :records)` should have 10 elements, uuid=>record map entries.
  ;;       All record entries should be map types.
  (is (and (= 10 (count (db/get-entities :records)))
           (every? true? (map map? (vals (db/get-entities :records))))))

  ;; Teardown db.
  (db/reset-db!)
  )

(comment

  (db/view-db)

  (db/reset-db!)

  (db/get-entities :records)

  (run-tests)

  )


