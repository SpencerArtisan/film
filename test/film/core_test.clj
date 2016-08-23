(ns film.core-test
  (:require [clojure.test :refer :all]
            [film.core :refer :all]))

(deftest str->int-test
  (testing 
    (is (= 42 (str->int "42")))))

(deftest str->float-test
  (testing 
    (is (= 42.0 (str->float "42")))))

(deftest convert-title-test
  (testing 
    (is (= "a title" (convert :title "a title")))))

(deftest convert-votes-test
  (testing 
    (is (= 42 (convert :votes "42")))))

(deftest rows-test
  (testing 
    (is (= ["line 1", "line 2"] (rows "line 1\nline 2")))))

(deftest parse-row-test
  (testing 
    (is (= '("5973", "7.9", "Pygmalion", "1938", "") (parse-row "      0000001311    5973   7.9  Pygmalion (1938)")))))

(deftest parse-test
  (testing 
    (is (= '(("5973", "7.9", "Pygmalion", "1938", ""), ("3518", "7.5", "The Bedford Incident", "1965", "")) 
           (parse "      0000001311    5973   7.9  Pygmalion (1938)\n      0000012310    3518   7.5  The Bedford Incident (1965)")))))

(deftest is-film-test
  (testing 
    (is (is-film {:extra ""})))
    (is (not (is-film {:extra "(VG)"})))
    (is (not (is-film {:extra "(V)"})))
    (is (not (is-film {:extra "(TV)"})))
    (is (not (is-film {})))
    (is (not (is-film {:extra "{}"}))))

(deftest is-rating-above-test
  (testing 
    (is (is-rating-above {:rating 6.0} 5.0)))
    (is (not (is-rating-above {:rating 4.0} 5.0))))

(deftest is-title-test
  (testing 
    (is (is-title {:title "title"} "title"))
    (is (is-title {:title "Title"} "title"))
    (is (not (is-title {:title "other"} "title")))))

(deftest is-enough-votes-test
  (testing 
    (is (is-enough-votes {:votes 2000}))
    (is (not (is-enough-votes {})))
    (is (not (is-enough-votes {:votes 100})))))

(deftest ratings-above-test
  (testing 
    (is (= [{:rating 6.0}] (ratings-above [{:rating 6.0}, {:rating 4.0}] 5.0)))))

(deftest filter-relevant-test
  (testing 
    (is (= [{:votes 2000, :extra ""}] 
           (filter-relevant [{:votes 2000, :extra ""} {:votes 3000, :extra "(VG)"} {:votes 10, :extra ""}])))))

(deftest row->map-test
  (testing 
    (is (= {:votes 5973 :rating 7.0 :title "Pygmalion" :year 1938 :extra ""} (row->map '("5973", "7.0", "Pygmalion", "1938", ""))))))

