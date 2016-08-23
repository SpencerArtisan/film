(ns film.core
  (:gen-class))

(def ratings-keys [:votes :rating :title :year :extra])

(defn str->int [str] (Integer. str))

(defn str->float [str] (Float. str))

(def conversions {:ignore identity
                  :votes str->int
                  :rating str->float
                  :title identity
                  :year str->int
                  :extra identity})

(defn convert 
  [ratings-key value]
  ((ratings-key conversions) value))

(defn rows
  [string]
  (clojure.string/split-lines string))

(def row-pattern #"^\s+(\S+)\s+(\S+)\s+(\S+)\s+\"?(.+?)\"?\s+\((\d+)\)(.*)")

(defn parse-row 
  [string]
  (map clojure.string/trim (drop 2 (re-find row-pattern string))))

(defn parse
  [string]
  (map parse-row (rows string)))

(defn is-rating-above
  [film rating]
  (and (:rating film) (> (:rating film) rating)))

(defn is-film
  [film]
   (and
     (:extra film)
     (not-any? #(clojure.string/includes? (:extra film) %) '("{", "TV", "VG", "V"))))

(defn is-enough-votes
  [film]
  (and (:votes film) (> (:votes film) 1000)))

(defn ratings-above
  [films rating]
  (filter #(is-rating-above % rating) films))

(defn film-filter
  [films]
  (filter #(and (is-film %) (is-enough-votes %)) films))

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
  (film-filter (mapify (parse (raw-data)))))

(defn pretty
  [film]
  (str (:title film) " (" (:rating film) ")" (:extra film)))

(defn command-r
  [args]
    (str 
      "Films with rating above " (second args) ":\n"
      (clojure.string/join "\n" (map pretty (ratings-above (films) (str->float (second args)))))))


(defn -main
  [& args]
  (println 
    (if (= "-r" (first args))
    (command-r args)
    "Unknown switch. USAGE: -r minimum-rating"))
)
