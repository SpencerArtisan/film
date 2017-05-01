(ns film3.find
  (:require [clj-http.client])
  (:require [film3.ui])
  (:require [film3.urls]))

(refer 'film3.urls)
(refer 'film3.ui)

(defn find2
  [url]
  (clj-http.client/get url {:as :json}))

(defn find-extra-film-detail
  [imdb-id]
  (:body (find2 (str "http://www.omdbapi.com/?i=" imdb-id))))

(defn director-filter
  [crew]
  (filter #(= "Director" (:job %)) crew))

(defn search-films-by-title
  [title]
  {:header (str "Films containing '" title "'") :data (reverse (sort-by :release_date (:results (:body (find2 (search-url "movie" title))))))})

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
  (let [body (:body (find2 (rest-url "movie" film-id "credits")))
        film-detail (film film-id)
        extra-detail (find-extra-film-detail (:imdb_id film-detail))
        combined-detail (merge film-detail extra-detail)]
    {:header combined-detail :data (concat (director-filter (:crew body)) (:cast body))}))

(defn find-person-by-id
  [person-id]
  {:header (person person-id) :data (reverse (sort-by :release_date (:cast (:body (find2 (rest-url "person" person-id "movie_credits"))))))})

(defn director-films
  [person-id]
  {:header (person person-id) :data (reverse (sort-by :release_date (director-filter (:crew (:body (find2 (rest-url "person" person-id "movie_credits")))))))})

(defn find-person-roles-by-id
  [person-id]
  (let [acting (find-person-by-id person-id)
        directing (director-films person-id)]
    {:header (:header acting) :data (reverse (sort-by :release_date (vec (concat (:data acting) (:data directing)))))}))

