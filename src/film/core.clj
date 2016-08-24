(ns film.core (:gen-class))

(require '[clojure.core.match :refer [match]])
(require '[clojure.java.io :refer [as-file]])
(require '[clojure.string :refer [split-lines trim includes? join]])

(defn str->int [str] (Integer. str))
(defn str->float [str] (Float. str))
