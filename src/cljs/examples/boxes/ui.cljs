(ns examples.boxes.ui
  (:require [gen.dom :as dom]
            [gen.random :as random]
            [gen.timers :as timers]
            [gen.lindenmayer]
            [quil.core :as q]
            [quil.middleware :as m]
            [cljs.core.async :as async :include-macros true]

            [clojure.test.check.generators :as gen]))

(enable-console-print!)

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
              (q/translate (/ w 2) (/ h 2))
              (q/rotate Math/PI)
              (q/background 255)
              (loop [ops ops]
                (when-let [op (first ops)]
                  (q/stroke-weight (q/random 10))
                  (q/stroke (q/random 255))
                  (cond
                    (number? op) (let [l (* 20 op)]
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

  (def chan (async/chan 1))

  (let [canvas      (dom/$ "canvas")
        units       [5 "R" 5 "R" 5 "R" 5]
        number-rule [#(number? %) (fn [v] ["L" 2 "R" v "R" 2 "L"])]
        split-rule  [#(number? %) (fn [v] (let [vv    (/ v 3)
                                                below (Math/floor vv)
                                                above (Math/ceil (- v below))]
                                            [above below]))]]
    (async/go-loop [units (gen.lindenmayer/rule-applier
                            units
                            [number-rule split-rule]
                            #(gen.lindenmayer/step %1 %2 (random/rand-no-repeat (count %1))))]
      (async/>! chan (first units))
      (recur (rest units)))

    (timers/immediate
      #(draw chan
             canvas
             (-> js/document .-documentElement .-clientWidth)
             (-> js/document .-documentElement .-clientHeight)))))


(defn init
  "Called on page load."
  []
  (println "on-load"))


(draw-it)


