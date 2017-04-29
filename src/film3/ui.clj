(ns film3.ui
  (require [lanterna.screen :as t]))

(def term (t/get-screen))
(t/start term)

(defn tdump
  [lines]
  (t/clear term)
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
    (if (= :enter next-key) 
      acc 
      (do
        (t/put-string term x y (str next-key))
        (t/redraw term)
        (recur (+ 1 x) y (str acc next-key)))
      )
  ))

(defn tin 
  [prompt]
  (t/clear term)
  (t/put-string term 1 0 prompt)
  (t/redraw term)
  (tin2 2 2 ""))

(defn select-row
  [lines]
  (t/put-string term 1 20 (str (get lines (get (t/get-cursor term) 1))))
  (t/redraw term)
  (case (tinchar2)
    :down (do
            (t/move-cursor term 0 (+ 1 (get (t/get-cursor term) 1)))
            (t/redraw term)
            (recur lines))
    :up (do
            (t/move-cursor term 0 (+ -1 (get (t/get-cursor term) 1)))
            (t/redraw term)
            (recur lines))
    :enter (get lines (get (t/get-cursor term) 1))
    (recur lines)))
