(ns film.core-test
  (:require [clojure.test :refer :all]
            [film.core :refer :all]))

(deftest str->int-test
  (testing 
    (is (= 42 (str->int "42")))))

(deftest str->float-test
  (testing 
    (is (= 42.0 (str->float "42")))))
