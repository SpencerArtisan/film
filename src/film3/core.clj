(ns film3.core
  (:require [film3.find])
  (:require [film3.pretty])
  (:require [film3.ui])
  (:require [clojure.string :as string])
  (:gen-class))

(refer 'film3.ui)
(refer 'film3.find)
(refer 'film3.pretty)

(def print-films
  (comp sort (partial map pretty-film) films))

(def print-persons
  (comp sort (partial map pretty-person) people))

(def print-characters
  (comp sort (partial map pretty-character) characters))

(def print-actor-films
  (comp sort (partial map pretty-actor-film) actor-films))

(def print-director-films
  (comp sort (partial map pretty-director-film) director-films))

(defn print-film-detail
  [film-id]
  (flatten (list
      (pretty-film-detail (film film-id))
      "Cast:"
      (print-characters film-id))))

(defn print-person-detail
  [person-id]
  (flatten (list
      (pretty-person-detail (person person-id))
      "Acted in:"
      (print-actor-films person-id)
      "Directed:" 
      (print-director-films person-id))))

(defn dump
  [lines]
  (println (string/join "\n" lines)))

(defn from-name 
  []
  (let [subject (tinchar "> Film (f) or Person (p)?")
        prompt (case subject \f "> Enter film name" \p "> Enter person name")
        name (tin prompt)
        finder (case subject \f print-films \p print-persons)]
    (tdump (finder name))))

(defn from-id 
  []
  (println "> Film (f) or Person (p)?")
  (let [subject (read-line)
        prompt (case subject "f" "> Enter film id" "p" "> Enter person id")
        _ (println prompt)
        id (read-line)
        finder (case subject "f" print-film-detail "p" print-person-detail)]
    (tdump (finder id))
    (recur)))

(defn -main [& args]
  (from-name)
  (from-id))

