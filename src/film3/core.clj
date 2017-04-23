(ns film3.core
  (:require [clj-http.client :as www])
  (:gen-class))

(defn results
  [response]
  (:results (:body response)))

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
  (take 8 (:cast (:body (find2 (noun-url "movie" film-id "credits"))))))

(defn actor-films
  [person-id]
  (:cast (:body (find2 (noun-url "person" person-id "movie_credits")))))

(defn director-films
  [person-id]
  (filter #(= "Director" (:job %)) (:crew (:body (find2 (noun-url "person" person-id "movie_credits"))))))

(defn film
  [film-id]
  (:body (find2 (noun-url2 "movie" film-id))))

(defn person
  [person-id]
  (:body (find2 (noun-url2 "person" person-id))))

(defn print-films
  [title]
  (prettify (films title) pretty-film))

(defn print-persons
  [name]
  (prettify (people name) pretty-person))

(defn print-characters
  [film-id]
  (prettify (characters film-id) pretty-character))

(defn print-film-detail
  [film-id]
  (println (pretty-film-detail (film film-id))) (println "\nCast:\n") (print-characters film-id))

(defn print-actor-films
  [person-id]
  (prettify (actor-films person-id) pretty-actor-film))

(defn print-director-films
  [person-id]
  (prettify (director-films person-id) pretty-director-film))

(defn print-person-detail
  [person-id]
  (println (pretty-person-detail (person person-id))) 
  (println "\nActed in:\n") (print-actor-films person-id)
  (println "\nDirected:\n") (print-director-films person-id))

(search-url "movie" "bedford")

(defn from-name []
  (println "> Film (f) or Person (p)?")
  (let [subject (read-line)
        prompt (case subject "f" "> Enter film name" "p" "> Enter person name")
        _ (println prompt)
        name (read-line)
        finder (case subject "f" print-films "p" print-persons)]
    (finder name))

(defn from-id []
  (println "> Film (f) or Person (p)?")
  (def subject (read-line))
  (def prompt (case subject "f" "> Enter film id" "p" "> Enter person id"))
  (println prompt)
  (def id (read-line))
  (def finder (case subject "f" print-film-detail "p" print-person-detail))
  (finder id)
  (recur)
  )

(defn -main [& args]
  (from-name)
  (from-id)
  )

