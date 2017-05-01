(ns film3.ui
  (require [lanterna.screen :as t]))

(def term (t/get-screen :auto {:cols 160 :rows 50 :font "Lucinda" :font-size 10}))

(t/start term)

(defn tquit
 []
 (t/stop term))

(defn wrap-line [text]
  (let [size (- (get (t/get-size term) 0) 1)]
    (clojure.pprint/cl-format nil (str "~{~<~%~1," size ":;~A~> ~}") (clojure.string/split text #" "))))

(defn wrap-line2 [text]
  (mapcat #(clojure.string/split-lines (wrap-line %)) (clojure.string/split-lines text)))

(defn tdump3
  [lines from-row]
  (t/move-cursor term 0 from-row)
  (let [indexed-lines (map-indexed vector lines)]
    (doseq [[row line] indexed-lines]
      (t/put-string term 1 (+ row from-row) line))
    (t/redraw term)))

(defn tdump
  [lines]
  (t/clear term)
  (tdump3 lines 0))

(defn tdump2
  [header lines]
  (let [header-lines (wrap-line2 header)
        header-rows (+ 1 (count header-lines))]
    (tdump header-lines)
    (t/put-string term 0 (- header-rows 1) (apply str (replicate 200 \-)))
    (tdump3 lines header-rows)
    header-rows))

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
;  (t/put-string term 0 0 (str data)) (t/redraw term))

(defn cursor-y
  []
  (get (t/get-cursor term) 1))

(defn select-row
  [first-row lines]
  (let [lines (vec lines)
        last-row (+ first-row (- (count lines) 1))
        y (min last-row (max (cursor-y) first-row))]
    (t/move-cursor term 0 y)
    (t/redraw term)
    (debug (get lines (- y first-row)))
    (defn down
      []
      (t/move-cursor term 0 (+ y 1))
      (t/redraw term)
      (select-row first-row lines))
    (defn up
      []
      (t/move-cursor term 0 (- y 1))
      (t/redraw term)
      (select-row first-row lines))
    (defn select
      []
      (str (get (get lines (- y first-row)) :id)))
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
      (select-row first-row lines))))


