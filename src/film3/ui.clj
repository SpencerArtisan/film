(ns film3.ui
  (require [clojure.string :as string])
  (require [lanterna.screen :as t]))

(def term 
  (t/get-screen :text {:cols 160 :rows 50 :font "Lucinda" :font-size 10}))

(defn start
  []
  (t/start term))

(defn quit
 []
 (t/stop term))

(defn refresh
  []
  (t/redraw term))

(defn cursor-y
  []
  (second (t/get-cursor term)))

(defn columns
  []
  (first (t/get-size term)))

(defn rows
  []
  (second (t/get-size term)))

(defn wrap-paragraph 
  [paragraph]
  (let [length (- (columns) 1)
        split-on-newlines (string/split-lines paragraph)]
    (defn wrap-line 
      [text]
      (clojure.pprint/cl-format nil (str "~{~<~%~1," length ":;~A~> ~}") (string/split text #" ")))
    (mapcat #(string/split-lines (wrap-line %)) split-on-newlines)))

(defn output
  [lines from-row color]
  (when-not (empty? lines)
    (t/put-string term 1 from-row (first lines) {:fg color})
    (recur (rest lines) (+ 1 from-row) color)))

(defn present
  [header-lines data-lines]
  (let [header-rows (+ 1 (count header-lines))]
    (t/clear term)
    (output header-lines 0 :default)
    (t/put-string term 0 (- header-rows 1) (apply str (replicate 200 \-)) {:fg :white})
    (output data-lines header-rows :blue)
    (refresh)))

(defn input-char
  ([prompt]
    (t/clear term)
    (t/put-string term 1 0 prompt)
    (t/move-cursor term (+ 2 (count prompt)) 0)
    (refresh)
    (input-char))
  ([]
    (t/get-key-blocking term)))

(defn input-string
  [prompt]
  (defn input-accumulator
    [x y acc]
    (t/move-cursor term x y)
    (refresh)
    (let [next-key (input-char)]
      (case next-key
        :enter
          (if (empty? acc) (recur x y acc) (apply str acc))
        :backspace
          (do
            (t/put-string term (- x 1) y " ")
            (recur (- x 1) y (drop-last acc)))
        ;default        
          (do
            (t/put-string term x y (str next-key))
            (recur (+ x 1) y (str acc next-key)))
          )))
  (t/clear term)
  (t/put-string term 1 0 prompt)
  (refresh)
  (input-accumulator (+ 2 (count prompt)) 0 ""))

(defn debug
  [& data]
  ())
  ;(t/put-string term 0 0 (str data)) (t/redraw term))

;(start)
;(select-row ["hello" "there"] ["0" "1" "2" "3" "4" "5" "6" "7"] [0 1 2 3 4 5 6 7] 2 2)
(defn select-row
  [header-lines data-lines data-ids offset selection-index]
  (let [data-lines (vec data-lines)
        data-ids (vec data-ids)
        header-line-count (+ 1 (count header-lines))
        data-line-count (count data-lines)
        max-cursor-y (- (rows) 1)
        max-selection-index (+ offset (- (rows) header-line-count))
        selection-index (min (- data-line-count 1) (max 0 selection-index))
        offset (max (- (+ 2 offset selection-index) max-selection-index) (min offset selection-index))
        new-cursor-y (+ (- selection-index offset) header-line-count)
        selected-item-id (get data-ids selection-index)]
    (t/move-cursor term 0 new-cursor-y)
    (present header-lines (drop offset data-lines))
    (debug selected-item-id)
    (defn move-vertical
      [delta-y]
      (select-row header-lines data-lines data-ids offset (+ selection-index delta-y)))
    (case (input-char)
      :down (move-vertical 1)
      \j (move-vertical 1)
      :up (move-vertical -1)
      \k (move-vertical -1)
      :enter selected-item-id
      :right selected-item-id
      \l selected-item-id
      :left -1
      \h -1
      :escape nil
      \q nil
      (recur header-lines data-lines data-ids offset selection-index))))


