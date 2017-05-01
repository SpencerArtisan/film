(ns film3.ui
  (require [lanterna.screen :as t]))

(def term (t/get-screen :text {:cols 160 :rows 50 :font "Lucinda" :font-size 10}))

(t/start term)

(defn tquit
 []
 (t/stop term))

(defn columns
  []
  (get (t/get-size term) 0))

(defn rows
  []
  (get (t/get-size term) 1))

(defn wrap-line [text]
  (let [size (- (get (t/get-size term) 0) 1)]
    (clojure.pprint/cl-format nil (str "~{~<~%~1," size ":;~A~> ~}") (clojure.string/split text #" "))))

(defn wrap-line2 [text]
  (mapcat #(clojure.string/split-lines (wrap-line %)) (clojure.string/split-lines text)))

(defn tdump3
  [lines from-row color]
  (let [indexed-lines (map-indexed vector lines)]
    (doseq [[row line] indexed-lines]
      (t/put-string term 1 (+ row from-row) line {:fg color}))))

(defn tdump
  [lines color]
  (t/clear term)
  (tdump3 lines 0 color))

(defn tdump2
  [header-lines lines]
  (let [header-rows (+ 1 (count header-lines))]
    (tdump header-lines :default)
    (t/put-string term 0 (- header-rows 1) (apply str (replicate 200 \-)) {:fg :white})
    (tdump3 lines header-rows :blue)
    (t/redraw term)))

(defn tinchar2
  []
  (t/get-key-blocking term))

(defn tinchar
  [prompt]
  (t/clear term)
  (t/put-string term 1 0 prompt)
  (t/move-cursor term (+ 2 (count prompt)) 0)
  (t/redraw term)
  (tinchar2))

(defn tin2
  [x y acc]
  (t/move-cursor term x y)
  (t/redraw term)
  (let [next-key (t/get-key-blocking term)]
    (case next-key
      :enter
        (if (empty? acc) (recur x y acc) (doall acc))
      :backspace
        (do
          (t/put-string term (- x 1) y " ")
          (t/redraw term)
          (recur (- x 1) y (drop-last acc)))
      ;default        
        (do
          (t/put-string term x y (str next-key))
          (t/redraw term)
          (recur (+ x 1) y (str acc next-key)))
        )))

(defn tin 
  [prompt]
  (t/clear term)
  (t/put-string term 1 0 prompt)
  (t/redraw term)
  (tin2 (+ 2 (count prompt)) 0 ""))

(defn debug
  [& data]
  ())
  ;(t/put-string term 0 0 (str data)) (t/redraw term))

(defn cursor-y
  []
  (get (t/get-cursor term) 1))

;(select-row ["hello" "there"] ["0" "1" "2" "3" "4" "5" "6" "7"] [0 1 2 3 4 5 6 7] 2 2)

(defn select-row
  [header-lines pretty-lines lines offset selection-index]
  (let [lines (vec lines)
        header-rows (+ 1 (count header-lines))
        data-rows (count lines)
        max-cursor-y (- (rows) 1)
        max-selection-index (+ offset (- (rows) header-rows))
        selection-index (min (- data-rows 1) (max 0 selection-index))
        offset (max (- (+ 2 offset selection-index) max-selection-index) (min offset selection-index))
        new-cursor-y (+ (- selection-index offset) header-rows)
        ]
    (t/move-cursor term 0 new-cursor-y)
    (tdump2 header-lines (drop offset pretty-lines))
;    (debug offset selection-index max-selection-index new-cursor-y max-cursor-y)
    (debug (get lines selection-index))
    (defn move-vertical
      [delta-y]
      (select-row header-lines pretty-lines lines offset (+ selection-index delta-y)))
    (defn down
      []
      (move-vertical 1))
    (defn up
      []
      (move-vertical -1))
    (defn select
      []
      (str (get (get lines selection-index) :id)))
    (case (tinchar2)
      :down (down)
      \j (down)
      :up (up)
      \k (up)
      :enter (select)
      :right (select)
      \l (select)
      :left -1
      \h -1
      :escape nil
      \q nil
      (select-row header-lines pretty-lines lines offset selection-index))))


