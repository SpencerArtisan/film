(ns film3.core
  (:require [film3.find])
  (:require [film3.pretty])
  (:require [film3.ui])
  (:require [clojure.string :as string])
  (:gen-class))

(refer 'film3.ui)
(refer 'film3.find)
(refer 'film3.pretty)

(defn pick
  "given a set of data, returns the id of the selected row, or -1 if Back is selected"
  [header data prettifier header-prettifier]
  (let [pretty-data (map prettifier data)
        pretty-header (header-prettifier header)
        header-rows (tdump2 pretty-header pretty-data)]
    (select-row header-rows data)))

(defn new-search
  []
  (let [data-type (case (tinchar "> Film (f) or Person (p)?") \f :films \p :people nil)]
     (if data-type 
       (let [word (apply str (tin (case data-type :films "> Enter film name" "> Enter person name")))]
         [{:id word :data-type data-type}])
       (recur))))

(defn navigate
  [stack]
  (let [id (:id (first stack))
        data-type (:data-type (first stack))
        data-finder (case data-type 
                       :film find-film-by-id 
                       :actor-film find-film-by-id 
                       :person-film person-credits-by-id
                       :character find-person-by-id
                       :person person-credits-by-id
                       :films search-films-by-title
                       :people search-people-by-name)
        {:keys [data header]} (data-finder id)
        sub-data-type (case data-type 
                        :films :film
                        :people :person
                        :film :character 
                        :person :person-film 
                        :character :actor-film 
                        :actor-film :character)
        prettifier (case sub-data-type 
                     :film pretty-film
                     :person pretty-person
                     :character pretty-character 
                     :person-film pretty-person-film
                     :actor-film pretty-actor-film)
        header-prettifier (case data-type
                            :film pretty-film-header
                            :actor-film pretty-film-header
                            :person-film pretty-person-header
                            :person pretty-person-header
                            :character pretty-person-header
                            str)
        new-id (pick header data prettifier header-prettifier)]
    (recur (case new-id
             -1 (if (= 1 (count stack)) (new-search) (rest stack))
             nil (new-search)
             (cons {:id new-id :data-type sub-data-type} stack)))))

(defn -main [& args]
  (navigate (new-search)))

