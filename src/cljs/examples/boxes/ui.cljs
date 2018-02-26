(ns examples.boxes.ui
  (:require [gen.dom :as dom]
            [gen.random :as random]
            [gen.timers :as timers]
            [gen.lindenmayer]
            [quil.core :as q]
            [quil.middleware :as m]
            [cljs.core.async :as async :include-macros true]
            [clojure.test.check.generators :as gen]))


(defn turtle
  "Pull operations from chan and draw a lindenmayer tree."
  [canvas w h chan]
  (q/sketch
    :host canvas
    :size [w h]
    :middleware [m/fun-mode]
    :mouse-clicked (fn [s e]
                     (update s :paused not))
    :setup (fn []
             (q/frame-rate 0)
             {:paused false})
    :update (fn [{:keys [paused] :as s}]
              (if (not paused)
                (assoc s :ops (async/poll! chan))
                s))
    :draw (fn [{:keys [ops paused]}]
            (when (not paused)
              (q/translate (/ w 2.4) (/ h 1.8))
              (q/rotate Math/PI)
              (q/background 255)
              (loop [ops ops]
                (when-let [op (first ops)]
                  (q/stroke-weight 2)
                  (if (> 0.5 (rand 1))
                    (q/stroke
                      (q/random 100 150)
                      (q/random 200 250)
                      (q/random 0))
                    (q/stroke
                      (q/random 200 250)
                      (q/random 0)
                      (q/random 100 150)))
                  (cond
                    (number? op) (let [l (* 20 op)]
                                   (q/point (* (rand) l)
                                            (* (rand) l))
                                   (q/line 0 0 0 l)
                                   (q/translate 0 l))
                    (= "R" op)   (q/rotate (/ Math/PI 2))
                    (= "L" op)   (q/rotate (/ Math/PI -2)))
                  (recur (rest ops))))))))


(defn draw
  "Draw a lindenmayer tree to canvas."
  [chan canvas w h]
  (turtle canvas w h chan))


(defn draw-it []

  (let [chan        (async/chan 1)
        canvas      (dom/$ "canvas")
        axiom       [5 "R" 5 "R" 5 "R" 5]
        number-rule [#(number? %) (fn [v] ["L" 2 "R" v "R" 2 "L"])]
        terms       (gen.lindenmayer/grow
                      axiom
                      [number-rule]
                      gen.lindenmayer/step-random)]

    (async/go-loop [terms terms]
      (async/>! chan (first terms))
      (recur (rest terms)))

    (timers/immediate
      #(draw chan canvas
             (-> js/document .-documentElement .-clientWidth)
             (-> js/document .-documentElement .-clientHeight)))))


(defn init
  "Called on page load."
  []
  (enable-console-print!)
  (println "on-load"))


(draw-it)


