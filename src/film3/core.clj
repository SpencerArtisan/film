(ns film3.core
  (:require [film3.find])
  (:require [film3.pretty])
  (:gen-class))

(refer 'film3.find)
(refer 'film3.pretty)

(defn print-films
  [title]
  (prettify (films title) pretty-film))

(defn print-persons
  [name]
  (prettify (people name) pretty-person))

(defn print-characters
  [film-id]
  (prettify (characters film-id) pretty-character))

(defn print-film-detail
  [film-id]
  (println (pretty-film-detail (film film-id))) (println "\nCast:\n") (print-characters film-id))

(defn print-actor-films
  [person-id]
  (prettify (actor-films person-id) pretty-actor-film))

(defn print-director-films
  [person-id]
  (prettify (director-films person-id) pretty-director-film))

(defn print-person-detail
  [person-id]
  (println (pretty-person-detail (person person-id))) 
  (println "\nActed in:\n") (print-actor-films person-id)
  (println "\nDirected:\n") (print-director-films person-id))

(defn from-name []
  (println "> Film (f) or Person (p)?")
  (let [subject (read-line)
        prompt (case subject "f" "> Enter film name" "p" "> Enter person name")
        _ (println prompt)
        name (read-line)
        finder (case subject "f" print-films "p" print-persons)]
    (finder name)))

(defn from-id []
  (println "> Film (f) or Person (p)?")
  (let [subject (read-line)
        prompt (case subject "f" "> Enter film id" "p" "> Enter person id")
        _ (println prompt)
        id (read-line)
        finder (case subject "f" print-film-detail "p" print-person-detail)]
    (finder id)
    (recur)))

(defn -main [& args]
  (from-name)
  (from-id))
