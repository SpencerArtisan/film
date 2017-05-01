(ns film3.find
  (:require [clj-http.client])
  (:require [film3.urls]))

(refer 'film3.urls)

(defn get-json
  [url]
  (clj-http.client/get url {:as :json}))

(defn director-filter
  [crew]
  (filter #(= "Director" (:job %)) crew))

(defn search-films-by-title
  [title]
  {:header (str "Films containing '" title "'") :data (reverse (sort-by :release_date (:results (:body (get-json (search-url "movie" title))))))})

(defn search-people-by-name
  [name]
  {:header (str "Names containing '" name "'") :data (:results (:body (get-json (search-url "person" name))))})

(defn find-extra-film-detail
  [imdb-id]
  (:body (get-json (imdb-data-url imdb-id))))

(defn find-film-detail
  [film-id]
  (:body (get-json (rest-url "movie" film-id))))

(defn find-person-detail
  [person-id]
  (:body (get-json (rest-url "person" person-id))))

(defn find-film-and-participants
  [film-id]
  (let [credits (:body (get-json (rest-url "movie" film-id "credits")))
        film-detail (find-film-detail film-id)
        extra-detail (find-extra-film-detail (:imdb_id film-detail))
        combined-detail (merge film-detail extra-detail)]
    {:header combined-detail :data (concat (director-filter (:crew credits)) (:cast credits))}))

(defn find-person-and-roles
  [person-id]
  (let [acting (:cast (:body (get-json (rest-url "person" person-id "movie_credits"))))
        directing (director-filter (:crew (:body (get-json (rest-url "person" person-id "movie_credits")))))
        person-detail (find-person-detail person-id)]
    {:header person-detail :data (reverse (sort-by :release_date (vec (concat acting directing))))}))

