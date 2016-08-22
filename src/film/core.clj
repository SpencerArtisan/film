(ns film.core
  (:gen-class))

(def ratings-keys [:ignore :votes :rating :title :year :extra])

(defn str->int [str] (Integer. str))

(defn str->float [str] (Float. str))

(def conversions {:ignore identity
                  :votes str->int
                  :rating str->float
                  :title identity
                  :year identity
                  :extra identity})

(defn convert 
  [ratings-key value]
  ((get conversions ratings-key) value))

(defn rows
  [string]
  (clojure.string/split-lines string))

(defn parse-row 
  [s]
  (map #(clojure.string/trim %) (drop 1 (re-find #"^\s+(\S+)\s+(\S+)\s+(\S+)\s+\"?(.+?)\"?\s+\((\d+)\)(.*)" s))))

(defn parse
  [string]
  (map #(parse-row %) (rows string)))

(def ratings
  (slurp "ratings.list" :encoding "ISO-8859-1"))

(defn ratings-above
  [minimum records]
  (filter #(and (:rating %) (> (:rating %) minimum)) records))

(defn is-film
  [film]
   (or 
     (not (:extra film))
     (and 
       (not (clojure.string/includes? (:extra film) "{"))
       (not (clojure.string/includes? (:extra film) "(VG)")))))

(defn film-filter
  [records]
  (filter #(and 
              (is-film %)
              (:votes %) 
              (> (:votes %) 10000))
          records))

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

(def ratings-data
  (film-filter (mapify (parse ratings))))

(defn pretty
  [film]
  (str (film :title) " (" (film :rating) ")" (film :extra)))

(defn command-r
  [args]
    (str 
      "Films with rating above " (second args)
      (clojure.string/join "\n" (map #(pretty %) (ratings-above (str->float (second args)) ratings-data)))))


(defn -main
  [& args]
  (println 
    (if (= "-r" (first args))
    (command-r args)
    "Unknown switch. USAGE: -r minimum-rating"))
)
