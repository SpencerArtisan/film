(require '[clj-http.client :as www])

(ns film3.core
  (:gen-class))

(def api-key "api_key=9f2001c43f5a2d845c5f0ea8689caef5")
(defn search-url [type query] (str "https://api.themoviedb.org/3/search/" type "?" api-key "&query=" query))
(defn noun-url [type id suffix] (str "https://api.themoviedb.org/3/" type "/" id "/" suffix "?" api-key ))
(defn noun-url2 [type id] (str "https://api.themoviedb.org/3/" type "/" id "?" api-key))
(defn pretty-film [film] (format "%-40s%.1f%10s" (:title film) (float (:vote_average film))(:id film)))
(defn pretty-person [person] (format "%-40s%10s" (:name person) (:id person) ))
(defn pretty-character [character] (format "%-40s%-80s%10s" (:name character) (:character character) (:id character)))
(defn pretty-actor-movie [movie] (format "%-15s%-45s%-45s%10s" (:release_date movie) (:title movie) (:character movie) (:id movie)))
(defn pretty-director-movie [movie] (format "%-15s%-30s%10s" (:release_date movie) (:title movie) (:id movie)))
(defn results [response] (:results (:body response)))
(defn find2 [url] (clj-http.client/get url {:as :json}))
(defn prettify [data formatter] (println (clojure.string/join "\n" (sort (map formatter data)))))

(defn movies [title]                  (:results (:body (find2 (search-url "movie" title)))))
(defn people [name]                   (:results (:body (find2 (search-url "person" name)))))
(defn characters [film-id]            (:cast (:body (find2 (noun-url "movie" film-id "credits")))))
(defn actor-movies [person-id]        (:cast (:body (find2 (noun-url "person" person-id "movie_credits")))))
(defn director-movies [person-id]     (filter #(= "Director" (:job %)) (:crew (:body (find2 (noun-url "person" person-id "movie_credits"))))))

(defn print-movies [title]                 (prettify (movies title) pretty-film))
(defn print-persons [name]                 (prettify (people name) pretty-person))
(defn print-characters [film-id]           (prettify (characters film-id) pretty-character))
(defn print-actor-movies [person-id]       (prettify (actor-movies person-id) pretty-actor-movie))
(defn print-director-movies [person-id]    (prettify (director-movies person-id) pretty-director-movie))
(defn print-person-movies [person-id]      (print-actor-movies person-id) (print-director-movies person-id))

(defn from-name []
  (println "> Film (f) or Person (p)?")
  (def subject (read-line))
  (def prompt (case subject "f" "> Enter film name" "p" "> Enter person name"))
  (println prompt)
  (def name (read-line))
  (def finder (case subject "f" print-movies "p" print-persons))
  (finder name)
)

(defn from-id []
  (println "> Film (f) or Person (p)?")
  (def subject (read-line))
  (def prompt (case subject "f" "> Enter film id" "p" "> Enter person id"))
  (println prompt)
  (def id (read-line))
  (def finder (case subject "f" print-characters "p" print-person-movies))
  (finder id)
  (recur)
)

(defn -main [& args]
  (from-name)
  (from-id)
)

