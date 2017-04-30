(ns film3.ui
  (require [lanterna.screen :as t]))

(def term (t/get-screen))
(t/start term)

(defn tdump
  [lines]
  (t/clear term)
  (t/move-cursor term 0 0)
  (let [indexed-lines (map-indexed vector lines)]
    (doseq [[row line] indexed-lines]
      (t/put-string term 1 row line))
    (t/redraw term)))

(defn tinchar2
  []
  (t/get-key-blocking term))

(defn tinchar
  [prompt]
  (t/clear term)
  (t/put-string term 1 0 prompt)
  (t/redraw term)
  (tinchar2))

(defn tin2
  [x y acc]
  (t/move-cursor term x y)
  (t/redraw term)
  (let [next-key (t/get-key-blocking term)]
    (case next-key
     :enter
        acc 
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
  (tin2 2 2 ""))


(defn debug
  [& data]
  (t/put-string term 0 25 (clojure.string/join "/" data))
  (t/redraw term))

(defn debug2
  [& data]
  (t/put-string term 0 27 (clojure.string/join "/" data))
  (t/redraw term))

(defn debug3
  [& data]
  (t/put-string term 0 29 (clojure.string/join "/" data))
  (t/redraw term))

(defn select-row
  [lines]
  (let [lines (vec lines)]
    (debug (get lines (get (t/get-cursor term) 1)))
    (defn down
      []
      (t/move-cursor term 0 (+ 1 (get (t/get-cursor term) 1)))
      (t/redraw term)
      (select-row lines))
    (defn up
      []
      (t/move-cursor term 0 (+ -1 (get (t/get-cursor term) 1)))
      (t/redraw term)
      (select-row lines))
    (defn select
      []
      (str (get (get lines (get (t/get-cursor term) 1)) :id)))

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
      :escape nil)))

