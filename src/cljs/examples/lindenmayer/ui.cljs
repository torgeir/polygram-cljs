(ns examples.lindenmayer.ui
  (:require [polygram.dom :as dom]
            [polygram.timers :as timers]
            [examples.lindenmayer.data :as lindenmayer.data]
            [quil.core :as q]
            [quil.middleware :as m]
            [cljs.core.async :as async :include-macros true]))


(defn s-draw [{:keys [w h op n first]}]
  (q/pop-matrix)
  (when first
    (q/translate (/ w 2) (/ h 1.2))
    (q/rotate Math/PI))
  (when op
    (condp = op
      "F" (do
            (q/stroke-weight (q/random (* 5 n)))
            (q/stroke (q/random 0 50)
                      (q/random 150 200)
                      (q/random 0 50))
            (let [l (q/random 1 (* 20 n))]
              (q/line 0 0 0 l)
              (q/translate 0 l)))
      "-" (q/rotate (rand -0.95))
      "+" (q/rotate (rand 0.90))
      "[" (q/push-matrix)
      "]" (q/pop-matrix)
      nil)
    (q/push-matrix)))


(defn s-update [{:keys [chan n] :as s}]
  (let [op (async/poll! chan)]
    (-> s
      (assoc :first false)
      (assoc :op op)
      (assoc :n (condp = op
                  "[" (* n 0.8)
                  "]" (/ n 0.8)
                  n)))))

(defn turtle
  "Pull operations from chan and draw a lindenmayer tree."
  [canvas w h chan]
  (q/sketch
    :host canvas
    :size [w h]
    :middleware [m/fun-mode]
    :setup (fn []
             (q/frame-rate 1000)
             (q/background 255)
             {:first true
              :n     1
              :w     w
              :h     h
              :chan  chan})
    :update #'s-update
    :draw #'s-draw))


(defn init
  "Called on page load."
  []
  (let [data (lindenmayer.data/generate "F" (lindenmayer.data/cool-trees 3) 5)
        h    (-> js/document .-documentElement .-clientHeight)
        w    (-> js/document .-documentElement .-clientWidth)]
    (timers/immediate
      #(turtle (dom/$ "canvas") w h (async/to-chan data)))))
