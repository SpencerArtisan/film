(ns film3.find
  (:require [clj-http.client])
  (:require [film3.urls]))

(refer 'film3.urls)

(defn find2
  [url]
  (clj-http.client/get url {:as :json}))

(defn films
  [title]
  (:results (:body (find2 (search-url "movie" title)))))

(defn people
  [name]
  (:results (:body (find2 (search-url "person" name)))))

(defn characters
  [film-id]
  (take 8 (:cast (:body (find2 (rest-url "movie" film-id "credits"))))))

(defn actor-films
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
