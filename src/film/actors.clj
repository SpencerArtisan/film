(ns film.actors (:gen-class))

(require '[clojure.core.match :refer [match]])
(require '[clojure.java.io :refer [as-file]])
(require '[clojure.string :refer [split-lines trim includes? join]])
(require '[film.core :refer :all])

(def row-pattern #"(.+)\t")

(defn find-name
  [data-row]
  (nth (re-find row-pattern data-row) 1))

(defn find-names
  [data]
  (filter identity (map find-name (split-lines data))))



