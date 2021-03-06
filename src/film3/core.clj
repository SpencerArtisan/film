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
  [header header-prettifier data data-prettifier]
  (let [data-lines (map data-prettifier data)
        header-lines (wrap-paragraph (header-prettifier header))
        data-ids (map :id data)]
    (select-row header-lines data-lines data-ids 0 0)))

(defn new-search
  []
  (let [data-type (case (input-char "Film (f), Person (p) or Quit (q)?") \f :film \p :person \q :quit nil)
        prompt (case data-type :film "Enter film name..." :person "Enter person name..." nil)]
    (case data-type
      :quit []
      nil (recur)
      [{:id (input-string prompt) :data-type data-type}])))

(defn navigate
  [stack]
  (when-not (empty? stack)
    (debug (first stack))
    (output ["Please wait...                "] 0 :default)
    (refresh)
    (let [id (:id (first stack))
          data-type (:data-type (first stack))
          data-finder (case data-type 
                         :film search-films-by-title
                         :person search-people-by-name
                         :film-participant find-film-and-participants
                         :person-role find-person-and-roles)
          sub-data-type (case data-type 
                          :film :film-participant
                          :person :person-role
                          :film-participant :person-role
                          :person-role :film-participant)
          data-prettifier (case data-type 
                       :film pretty-film
                       :person pretty-person
                       :film-participant pretty-participant
                       :person-role pretty-person-role)
          header-prettifier (case data-type
                              :film-participant pretty-film-header
                              :person-role pretty-person-header
                              str)
          {:keys [header data]} (data-finder id)
          new-id (pick header header-prettifier data data-prettifier)]
      (recur (case new-id
               -1 (if (= 1 (count stack)) (new-search) (rest stack))
               nil (new-search)
               (cons {:id new-id :data-type sub-data-type} stack))))))

(defn -main 
  [& args]
  (start)
  (navigate (if (empty? args) (new-search) [{:id (first args) :data-type :film}]))
  (quit))

