(ns film3.core
  (:require [film3.find])
  (:require [film3.pretty])
  (:require [film3.ui])
  (:require [clojure.string :as string])
  (:gen-class))

(refer 'film3.ui)
(refer 'film3.find)
(refer 'film3.pretty)

(defn search-type 
  []
  (case (tinchar "> Film (f) or Person (p)?") \f :film :person))

(defn search-word
  [data-type]
  (let [prompt (case data-type :film "> Enter film name" "> Enter person name")]
    (tin prompt)))

(defn searcher
  [data-type]
    (case data-type :film search-films-by-title search-people-by-name))

(defn finder
  [data-type]
    (case data-type :film find-film-by-id :actor-film find-film-by-id find-person-by-id))

(defn pick
  [data prettifier]
  (let [pretty-data (map prettifier data)]
    (tdump pretty-data)
    (debug3 prettifier)
    (select-row data)))

(defn navigate
  [id data-type]
  (let [data-finder (finder data-type)
        data (data-finder id)
        sub-data-type (case data-type :film :character :person :actor-film :character :actor-film :actor-film :character)
        prettifier (case sub-data-type :character pretty-character :actor-film pretty-actor-film :person pretty-person :film pretty-film)
        new-id (pick data prettifier)]
    (recur new-id sub-data-type)))

(defn -main [& args]
   (let [data-type (search-type)
         word (search-word data-type)
         data-searcher (searcher data-type)
         data (data-searcher word)
         prettifier (case data-type :film pretty-film pretty-person)
         id (pick data prettifier)]
      (navigate id data-type)))

