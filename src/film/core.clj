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

;(defn rows
;  [string]
;  (split-lines string))
(def rows split-lines)

;(defn parse-row 
;  [string]
;  (map trim (drop 2 (re-find row-pattern string))))
(def parse-row 
  (comp (partial map trim) (partial drop 2) (partial re-find row-pattern)))

;(defn parse-row 
;  [string]
;  (map trim (drop 2 (re-find row-pattern string))))
(def parse-row 
  (comp (partial map trim) (partial drop 2) (partial re-find row-pattern)))

;(defn parse-file
;  [string]
;  (map parse-row (rows string)))
(def parse-file
  (comp (partial map parse-row) rows))

(defn is-title?
  [film title]
  (and (:title film) (re-find (re-pattern (str "(?i)" title)) (:title film)))) 

(defn is-rating-above?
  [film rating]
  (and (:rating film) (>= (:rating film) rating)))

(defn is-film?
  [film]
   (and
     (:extra film)
     (not-any? #(includes? (:extra film) %) '("{", "TV", "VG", "V"))))

;(defn is-enough-votes?
;  [film]
;  (and (:votes film) (> (:votes film) 1000)))
(def is-enough-votes?
  (every-pred :votes #(> (:votes %) 1000)))

;(defn is-relevant?
;  [film]
;  (and (is-film? film) (is-enough-votes? film)))
(def is-relevant?
  (every-pred is-film? is-enough-votes?))

(defn titles-matching
  [films title]
  (filter #(is-title? % title) films))

(defn ratings-above
  [films rating]
  (filter #(is-rating-above? % rating) films))

;(defn filter-relevant
;  [films]
;  (filter is-relevant? films))
(def filter-relevant
  (partial filter is-relevant?))

(defn row->map
  [unmapped-row]
         (try
           (reduce (fn [row-map [ratings-key value]]
                   (assoc row-map ratings-key (convert ratings-key value)))
                 {}
                 (map vector ratings-keys unmapped-row))
           (catch Exception e (println "Bad row:") (println unmapped-row))))

;(defn mapify
;  [rows]
;  (map row->map rows))
(def mapify 
  (partial map row->map))

(defn read-raw-data
  []
  (slurp "ratings.list" :encoding "ISO-8859-1"))

(def raw-data (memoize read-raw-data))

;(defn films
;  []
;  (sort-by :title (filter-relevant (mapify (parse-file (raw-data))))))
(def films
  (comp (partial sort-by :title) filter-relevant mapify parse-file raw-data))

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

;(defn command-search
;  [films title]
;      (join "\n" (map pretty (titles-matching films title))))
(def command-search
  (comp (partial join "\n") (partial map pretty) titles-matching))

(defn -main
  [& args]
  (let [films (films-or-cache)]
    (println 
      (match (into [] args)
        ["-r" rating] (command-r films rating)
        [title] (command-search films title)
        :else "Unknown switch. USAGES:\n name: searches for move\n -r minimum-rating movies with ratings equal to or above"))))
