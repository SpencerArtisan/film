(require '[clj-http.client :as www])

(ns film3.core
  (:gen-class))

(def root-url                         "https://api.themoviedb.org/3/")
(def api-key                          "api_key=9f2001c43f5a2d845c5f0ea8689caef5")
(defn search-url [type query]         (str root-url "search/" type "?" api-key "&query=" query))
(defn noun-url [type id suffix]       (str root-url type "/" id "/" suffix "?" api-key))
(defn noun-url2 [type id]             (str root-url type "/" id "?" api-key))
(defn results [response]              (:results (:body response)))
(defn find2 [url]                     (clj-http.client/get url {:as :json}))

(defn pretty-film-detail [film]       (format "%s\nRating: %.1f\n\n%s\n\nhttp://www.imdb.com/title/%s" (:title film) (float (:vote_average film)) (:overview film) (:imdb_id film)))
(defn pretty-person-detail [person]       (format "%s\n%s - %s\n%s" (:name person) (:birthday person) (:deathday person) (:biography person)))
(defn pretty-film [film]              (format "%-40s%.1f%10s" (:title film) (float (:vote_average film)) (:id film)))
(defn pretty-person [person]          (format "%-40s%10s" (:name person) (:id person) ))
(defn pretty-character [character]    (format "%-40s%-80s%10s" (:name character) (:character character) (:id character)))
(defn pretty-actor-film [film]        (format "%-15s%-45s%-45s%10s" (:release_date film) (:title film) (:character film) (:id film)))
(defn pretty-director-film [film]     (format "%-15s%-30s%10s" (:release_date film) (:title film) (:id film)))
(defn prettify [data formatter]       (println (clojure.string/join "\n" (sort (map formatter data)))))

(defn films [title]                   (:results (:body (find2 (search-url "movie" title)))))
(defn people [name]                   (:results (:body (find2 (search-url "person" name)))))
(defn characters [film-id]            (take 8 (:cast (:body (find2 (noun-url "movie" film-id "credits"))))))
(defn actor-films [person-id]         (:cast (:body (find2 (noun-url "person" person-id "movie_credits")))))
(defn director-films [person-id]      (filter #(= "Director" (:job %)) (:crew (:body (find2 (noun-url "person" person-id "movie_credits"))))))
(defn film [film-id]                  (:body (find2 (noun-url2 "movie" film-id))))
(defn person [person-id]              (:body (find2 (noun-url2 "person" person-id))))

(defn print-films [title]                 (prettify (films title) pretty-film))
(defn print-persons [name]                (prettify (people name) pretty-person))
(defn print-characters [film-id]          (prettify (characters film-id) pretty-character))
(defn print-film-detail [film-id]         (println (pretty-film-detail (film film-id))) (println "\nCast:\n") (print-characters film-id))
(defn print-actor-films [person-id]       (prettify (actor-films person-id) pretty-actor-film))
(defn print-director-films [person-id]    (prettify (director-films person-id) pretty-director-film))
(defn print-person-detail [person-id]     (println (pretty-person-detail (person person-id))) 
                                          (println "\nActed in:\n") (print-actor-films person-id)
                                          (println "\nDirected:\n") (print-director-films person-id))

(search-url "movie" "bedford")

(defn from-name []
  (println "> Film (f) or Person (p)?")
  (def subject (read-line))
  (def prompt (case subject "f" "> Enter film name" "p" "> Enter person name"))
  (println prompt)
  (def name (read-line))
  (def finder (case subject "f" print-films "p" print-persons))
  (finder name)
)

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

