(ns film3.urls)

(def ^:private root-url "https://api.themoviedb.org/3/")

(def ^:private api-key "api_key=9f2001c43f5a2d845c5f0ea8689caef5")

(defn search-url
  "Build a url for nouns"
  [type query]
  (str root-url "search/" type "?" api-key "&query=" query))

(defn noun-url
  "Build a url for a specific noun"
  [type id suffix]
  (str root-url type "/" id "/" suffix "?" api-key))

(defn noun-url2
  "Build a url for a noun"
  [type id]
  (str root-url type "/" id "?" api-key))
