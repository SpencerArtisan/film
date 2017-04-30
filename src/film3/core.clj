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
  (case (tinchar "> Film (f) or Person (p)?") \f :films :people))

(defn search-word
  [data-type]
  (let [prompt (case data-type :film "> Enter film name" "> Enter person name")]
    (tin prompt)))

(defn finder
  [data-type]
    (case data-type 
      :film find-film-by-id 
      :actor-film find-film-by-id 
      :character find-person-by-id
      :films search-films-by-title
      :people search-people-by-name))

(defn pick
  "given a set of data, returns the id of the selected row, or -1 if Back is selected"
  [data prettifier]
  (let [pretty-data (map prettifier data)]
    (tdump pretty-data)
    (debug3 prettifier)
    (select-row data)))

(defn navigate
  [stack]
  (debug3 (first stack))
  (tinchar2)
  (let [id (:id (first stack))
        data-type (:data-type (first stack))
        data-finder (finder data-type)
        data (data-finder id)
        sub-data-type (case data-type 
                        :films :film
                        :people :person
                        :film :character 
                        :person :actor-film 
                        :character :actor-film 
                        :actor-film :character)
        prettifier (case sub-data-type 
                     :film pretty-film
                     :person pretty-person 
                     :character pretty-character 
                     :actor-film pretty-actor-film)
        new-id (pick data prettifier)]
    (recur (if (= -1 new-id)
             (if (= 1 (count stack)) stack (rest stack))
             (cons {:id new-id :data-type sub-data-type} stack)))))

(defn -main [& args]
   (let [data-type (search-type)
         word (search-word data-type)]
      (navigate [{:id word :data-type data-type}])))

