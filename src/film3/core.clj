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
  (let [data-type (case (tinchar "Film (f), Person (p) or Quit (q)?") \f :film \p :person \q :quit nil)
        prompt (case data-type :film "Enter film name..." :person "Enter person name..." nil)
        word (if prompt (apply str (tin prompt)))]
    (case data-type
      :quit []
      nil (recur)
      [{:id word :data-type data-type}])))

(defn navigate
  [stack]
  (if (not (empty? stack)) 
   (do 
    (debug (first stack))
    (tdump ["Please wait..."])
    (let [id (:id (first stack))
          data-type (:data-type (first stack))
          data-finder (case data-type 
                         :film search-films-by-title
                         :person search-people-by-name
                         :film-participant find-film-by-id
                         :person-role find-person-roles-by-id)
          {:keys [data header]} (data-finder id)
          sub-data-type (case data-type 
                          :film :film-participant
                          :person :person-role
                          :film-participant :person-role
                          :person-role :film-participant)
          prettifier (case data-type 
                       :film pretty-film
                       :person pretty-person
                       :film-participant pretty-participant
                       :person-role pretty-person-role)
          header-prettifier (case data-type
                              :film-participant pretty-film-header
                              :person-role pretty-person-header
                              str)
          new-id (pick header data prettifier header-prettifier)]
      (recur (case new-id
               -1 (if (= 1 (count stack)) (new-search) (rest stack))
               nil (new-search)
               (cons {:id new-id :data-type sub-data-type} stack)))))))

(defn -main [& args]
  (navigate (new-search))
  (tquit))

