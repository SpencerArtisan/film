(ns film3.pretty
  (:require [film3.ui]))

(refer 'film3.ui)

(defn pretty-film-header
  [{:keys [title imdbRating overview imdb_id]}]
  (format "%s\nRating: %s\n\n%s\n\nhttp://www.imdb.com/title/%s" title imdbRating overview imdb_id))

(defn pretty-person-header
  [{:keys [name birthday deathday biography]}]
  (format "%s\n%s - %s\n%s" name (or birthday "") (or deathday "") (if biography (clojure.string/replace biography "From Wikipedia, the free encyclopedia. " "") "")))

(defn pretty-film
  [{:keys [title release_date]}]
  (format "%-15s%-45s" release_date title))

(defn pretty-person
  [{:keys [name]}]
  (format "%-40s" name))

(defn pretty-participant
  [{:keys [name character]}]
  (format "%-40s%-80s" name (if character character "Director")))

(defn pretty-actor-film
  [{:keys [release_date title character]}]
  (format "%-15s%-45s%-45s" release_date title character))

(defn pretty-director-film
  [{:keys [release_date title]}]
  (format "%-15s%-45s" release_date title))

(defn pretty-person-role
  [{character :character :as person}]
  (debug person)
  (if character (pretty-actor-film person) (pretty-actor-film (assoc person :character "Director"))))

