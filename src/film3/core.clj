(require '[clj-http.client :as www])

(ns film3.core
  (:gen-class))

(defn search-url [type query] (str "https://api.themoviedb.org/3/search/" type "?api_key=9f2001c43f5a2d845c5f0ea8689caef5&query=" query))
(defn noun-url [type id suffix] (str "https://api.themoviedb.org/3/" type "/" id "/" suffix "?api_key=9f2001c43f5a2d845c5f0ea8689caef5"))
(def movie-type "movie")
(def person-type "person")
(defn pretty-film [film] (format "%-40s%.1f%10s" (:title film) (float (:vote_average film))(:id film)))
(defn pretty-person [person] (format "%-40s%10s" (:name person) (:id person) ))
(defn pretty-character [character] (format "%-40s%-80s%10s" (:name character) (:character character) (:id character)))
(defn pretty-actor-movie [movie] (format "%-15s%-45s%-45s%10s" (:release_date movie) (:title movie) (:character movie) (:id movie)))
(defn pretty-director-movie [movie] (format "%-15s%-30s%10s" (:release_date movie) (:title movie) (:id movie)))
(defn results [response] (:results (:body response)))
(defn find2 [url] (clj-http.client/get url {:as :json}))
(defn prettify [data formatter] (println (clojure.string/join "\n" (sort (map formatter data)))))

(defn find-and-prettify [prettifier & find-args] (prettify (results (find2 (apply search-url find-args))) prettifier))

(defn find-movie [title] (find-and-prettify pretty-film movie-type title))
(defn find-person [name] (find-and-prettify pretty-person person-type name))

(defn movie-actors [id] (:cast (:body (find2 (noun-url "movie" id "credits")))))
(defn actor-movies [id] (:cast (:body (find2 (noun-url "person" id "movie_credits")))))
(defn director-movies [id] (filter #(= "Director" (:job %)) (:crew (:body (find2 (noun-url "person" id "movie_credits"))))))

(defn pretty-actors [movie-id] (prettify (movie-actors movie-id) pretty-character))
(defn pretty-actor-movies [person-id] (prettify (actor-movies person-id) pretty-actor-movie))
(defn pretty-director-movies [person-id] (prettify (director-movies person-id) pretty-director-movie))
(defn pretty-person [person-id] (pretty-actor-movies person-id) (pretty-director-movies person-id))

(defn xxx []
  (find-movie "fight")
  (find-person "bergman")
  (pretty-actors 29453)
  (pretty-actor-movies 4111)
  (pretty-director-movies 6648)
  (actor-movies 4111)
)

(defn from-name []
  (println "> Film (f) or Person (p)?")
  (def subject (read-line))
  (def prompt (case subject "f" "> Enter film name" "p" "> Enter person name"))
  (println prompt)
  (def name (read-line))
  (def finder (case subject "f" find-movie "p" find-person))
  (finder name)
)

(defn from-id []
  (println "> Film (f) or Person (p)?")
  (def subject (read-line))
  (def prompt (case subject "f" "> Enter film id" "p" "> Enter person id"))
  (println prompt)
  (def id (read-line))
  (def finder (case subject "f" pretty-actors "p" pretty-person))
  (finder id)
  (recur)
)

(defn -main
  [& args]

  (from-name)
  (from-id)
)
