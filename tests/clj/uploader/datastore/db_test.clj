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
    (is (= record  (db/get-record-by-id id)))))


(comment

  (db/view-db)

  (db/reset-db!)

  (run-tests)

  )


