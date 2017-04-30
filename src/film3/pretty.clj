(ns film3.pretty
  (:require [film3.ui]))

(refer 'film3.ui)

(defn pretty-film-detail
  [{:keys [title vote_average overview imdb_id]}]
  (format "%s\nRating: %.1f\n\n%s\n\nhttp://www.imdb.com/title/%s" title (float vote_average) overview imdb_id))

(defn pretty-person-detail
  [{:keys [name birthday deathday biography]}]
  (format "%s\n%s - %s\n%s" name birthday deathday biography))

(defn pretty-film
  [{:keys [title release_date]}]
  (format "%-40s%10s" title release_date))

(defn pretty-person
  [{:keys [name]}]
  (format "%-40s" name))

(defn pretty-character
  [{:keys [name character]}]
  (format "%-40s%-80s" name character))

(defn pretty-actor-film
  [{:keys [release_date title character]}]
  (format "%-15s%-45s%-45s" release_date title character))

(defn pretty-director-film
  [{:keys [release_date title]}]
  (format "%-15s%-30s" release_date title))

(defn pretty-person-film
  [{character :character :as person}]
  (if character (pretty-actor-film person) (pretty-actor-film (assoc person :character "Director"))))

(defn prettify 
  [data-type data]
  (let [prettifier (case data-type :film pretty-film :person pretty-person :character pretty-character)]
    (map prettifier data)))
