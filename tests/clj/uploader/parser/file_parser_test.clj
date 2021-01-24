(ns uploader.parser.file-parser-test
  (:require [uploader.parser.file-parser :as fp]
            [clojure.test :refer [deftest is run-tests]]))
 

(deftest delimeter-type-test
  (is (and (= "\\|" (fp/delimiter-type "fname|lname|gender|color|1993-01-01"))
           (= "\\|" (fp/delimiter-type "fname | lname |gender |color|1993-01-01"))
           (= "," (fp/delimiter-type "fname,lname,gender,color,1993-01-01"))
           (= "," (fp/delimiter-type " fname , lname,gender ,color    , 1993-01-01"))
           (= "\\s+" (fp/delimiter-type "fname lname gender color 1993-01-01"))
           )))


(comment

  (run-tests)


  )
