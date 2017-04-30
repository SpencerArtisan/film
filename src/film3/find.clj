(ns film3.find
  (:require [clj-http.client])
  (:require [film3.ui])
  (:require [film3.urls]))

(refer 'film3.urls)
(refer 'film3.ui)

(defn find2
  [url]
  (debug2 url)
  (tinchar2)
  (clj-http.client/get url {:as :json}))

(defn search-films-by-title
  [title]
  (:results (:body (find2 (search-url "movie" title)))))

(defn search-people-by-name
  [name]
  (:results (:body (find2 (search-url "person" name)))))

(defn find-film-by-id
  [film-id]
  (take 8 (:cast (:body (find2 (rest-url "movie" film-id "credits"))))))

(defn find-person-by-id
  [person-id]
  (:cast (:body (find2 (rest-url "person" person-id "movie_credits")))))

(defn director-films
  [person-id]
  (filter #(= "Director" (:job %)) (:crew (:body (find2 (rest-url "person" person-id "movie_credits"))))))

(defn film
  [film-id]
  (:body (find2 (rest-url "movie" film-id))))

(defn person
  [person-id]
  (:body (find2 (rest-url "person" person-id))))

