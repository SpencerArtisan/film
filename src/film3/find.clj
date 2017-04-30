(ns film3.find
  (:require [clj-http.client])
  (:require [film3.ui])
  (:require [film3.urls]))

(refer 'film3.urls)
(refer 'film3.ui)

(defn find2
  [url]
  (clj-http.client/get url {:as :json}))

(defn search-films-by-title
  [title]
  {:header (str "Films containing '" title "'") :data (:results (:body (find2 (search-url "movie" title))))})

(defn search-people-by-name
  [name]
  {:header (str "Names containing '" name "'") :data (:results (:body (find2 (search-url "person" name))))})

(defn film
  [film-id]
  (:body (find2 (rest-url "movie" film-id))))

(defn person
  [person-id]
  (:body (find2 (rest-url "person" person-id))))

(defn find-film-by-id
  [film-id]
  {:header (film film-id) :data (take 8 (:cast (:body (find2 (rest-url "movie" film-id "credits")))))})

(defn find-person-by-id
  [person-id]
  {:header (person person-id) :data (:cast (:body (find2 (rest-url "person" person-id "movie_credits"))))})

(defn director-films
  [person-id]
  {:header (person person-id) :data (filter #(= "Director" (:job %)) (:crew (:body (find2 (rest-url "person" person-id "movie_credits")))))})

(defn person-credits-by-id
  [person-id]
  {:header (person person-id) :data (vec (concat (find-person-by-id person-id) (director-films person-id)))})

