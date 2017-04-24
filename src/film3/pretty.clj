(ns film3.pretty)

(defn pretty-film-detail
  [{:keys [title vote_average overview imdb_id]}]
  (format "%s\nRating: %.1f\n\n%s\n\nhttp://www.imdb.com/title/%s" title (float vote_average) overview imdb_id))

(defn pretty-person-detail
  [{:keys [name birthday deathday biography]}]
  (format "%s\n%s - %s\n%s" name birthday deathday biography))

(defn pretty-film
  [{:keys [title vote_average id]}]
  (format "%-40s%.1f%10s" title (float vote_average) id))

(defn pretty-person
  [{:keys [name id]}]
  (format "%-40s%10s" name id))

(defn pretty-character
  [{:keys [name character id]}]
  (format "%-40s%-80s%10s" name character id))

(defn pretty-actor-film
  [{:keys [release_date title character id]}]
  (format "%-15s%-45s%-45s%10s" release_date title character id))

(defn pretty-director-film
  [{:keys [release_date title id]}]
  (format "%-15s%-30s%10s" release_date title id))

