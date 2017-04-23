(ns film3.urls
  (:require [clojure.string :as string]))

(def ^:private root-url "https://api.themoviedb.org/3/")

(def ^:private api-key "api_key=9f2001c43f5a2d845c5f0ea8689caef5")

(defn rest-url
  "Build a url for a restful resource"
  [& parts]
  (str root-url (string/join "/" parts) "?" api-key))

(defn search-url
  "Build a url for nouns"
  [type query]
  (str (rest-url "search" type) "&query=" query))
