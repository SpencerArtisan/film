(ns film.actors-test
  (:require [clojure.test :refer :all]
            [film.actors :refer :all]))

(deftest find-names-test
  (testing 
    (is (= '("Black, Jack") (find-names "Black, Jack\ta film")))
    (is (= '("Black, Jack" "Bowie, David") (find-names "Black, Jack\ta film\n\nBowie, David\ta film")))))

