(ns film.core (:gen-class))

(require '[clojure.core.match :refer [match]])
(require '[clojure.java.io :refer [as-file]])
(require '[clojure.string :refer [split-lines trim includes? join]])

(defn str->int [str] (Integer. str))
(defn str->float [str] (Float. str))

(def ratings-keys [:votes :rating :title :year :extra])

(def conversions {:ignore identity
                  :votes str->int
                  :rating str->float
                  :title identity
                  :year str->int
                  :extra identity})

(def row-pattern #"^\s+(\S+)\s+(\S+)\s+(\S+)\s+\"?(.+?)\"?\s+\((\d+)\)(.*)")

(def cache ".ratings.cache")

(defn convert 
  [ratings-key value]
  ((ratings-key conversions) value))

(defn rows
  [string]
  (split-lines string))

(defn parse-row 
  [string]
  (map trim (drop 2 (re-find row-pattern string))))

(defn parse
  [string]
  (map parse-row (rows string)))

(defn is-title
  [film title]
  (and (:title film) (re-find (re-pattern (str "(?i)" title)) (:title film)))) 

(defn is-rating-above
  [film rating]
  (and (:rating film) (>= (:rating film) rating)))

(defn is-film
  [film]
   (and
     (:extra film)
     (not-any? #(includes? (:extra film) %) '("{", "TV", "VG", "V"))))

(defn is-enough-votes
  [film]
  (and (:votes film) (> (:votes film) 1000)))

(defn is-relevant
  [film]
  (and (is-film film) (is-enough-votes film)))

(defn titles-matching
  [films title]
  (filter #(is-title % title) films))

(defn ratings-above
  [films rating]
  (filter #(is-rating-above % rating) films))

(defn filter-relevant
  [films]
  (filter is-relevant films))

(defn row->map
  [unmapped-row]
         (try
           (reduce (fn [row-map [ratings-key value]]
                   (assoc row-map ratings-key (convert ratings-key value)))
                 {}
                 (map vector ratings-keys unmapped-row))
           (catch Exception e (println "Bad row:") (println unmapped-row))))

(defn mapify
  [rows]
  (map row->map rows))

(defn raw-data
  []
  (slurp "ratings.list" :encoding "ISO-8859-1"))

(defn films
  []
  (sort-by :title (filter-relevant (mapify (parse (raw-data))))))

(defn films-or-cache
  []
  (if (.exists (as-file cache))
      (read-string (slurp cache))
      (let [films (films)]
          (spit cache (with-out-str (pr films)))
          (identity films))))

(defn pretty
  [film]
  (str (:rating film) "   " (:title film) " (" (:year film) ")" (:extra film)))

(defn command-r
  [films rating]
    (str 
      "Films with rating above " rating ":\n"
      (join "\n" (map pretty (ratings-above films (str->float rating))))))

(defn command-search
  [films title]
      (join "\n" (map pretty (titles-matching films title))))

(defn -main
  [& args]
  (let [films (films-or-cache)]
    (println 
      (match (into [] args)
        ["-r" rating] (command-r films rating)
        [title] (command-search films title)
        :else "Unknown switch. USAGES:\n name: searches for move\n -r minimum-rating movies with ratings equal to or above"))))
