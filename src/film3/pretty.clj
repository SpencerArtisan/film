(ns film3.pretty)

(defn pretty-film-detail
  ""
  [film]
  (format "%s\nRating: %.1f\n\n%s\n\nhttp://www.imdb.com/title/%s"           (:title film)
          (float (:vote_average film))
          (:overview film)
          (:imdb_id film)))

(defn pretty-person-detail
  [person]
  (format "%s\n%s - %s\n%s"
          (:name person)
          (:birthday person)
          (:deathday person)
          (:biography person)))

(defn pretty-film
  [film]
  (format "%-40s%.1f%10s"
          (:title film)
          (float (:vote_average film))
          (:id film)))

(defn pretty-person
  [person]
  (format "%-40s%10s"
          (:name person)
          (:id person)))

(defn pretty-character
  [character]
  (format "%-40s%-80s%10s"
          (:name character)
          (:character character)
          (:id character)))

(defn pretty-actor-film
  [film]
  (format "%-15s%-45s%-45s%10s"
          (:release_date film)
          (:title film)
          (:character film)
          (:id film)))

(defn pretty-director-film
  [film]
  (format "%-15s%-30s%10s"
          (:release_date film)
          (:title film)
          (:id film)))

