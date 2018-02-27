(ns examples.blueprint.ui
  (:require [terws.dom :as dom]
            [terws.timers :as timers]
            [terws.core :refer [grow step-random]]
            [terws.random :as rng]
            [quil.core :as q]
            [quil.middleware :as m]
            [cljs.core.async :as async :include-macros true]))

(def axiom [5 "R" 5 "R" 5 "R" 5 "R"])

(def expand-rule [number? #(let [r (rand %)] ["L" r "R" % "R" r "L"])])

(def thicken-rule [number? #(let [a %] ["+" a "-"])])

(def split-rule [number? #(let [m (int (rand %))] [m (- % m)])])

(def terms (grow
             axiom
             [expand-rule expand-rule expand-rule thicken-rule split-rule]
             step-random))

(defn turtle
  [unit]
  (if (number? unit)
    (do
      (q/line 0 0 (* unit 8) 0)
      (q/translate (* unit 8) 0))
    (condp = unit
      "L" (q/rotate (/ Math/PI 2))
      "R" (q/rotate (/ Math/PI -2))
      "+" (q/stroke-weight 3)
      "-" (q/stroke-weight 1)
      nil)))


(defn sketch
  [canvas w h]
  (q/sketch
    :host canvas
    :size [w h]
    :middleware [m/fun-mode]
    :setup (fn []
             (q/frame-rate 10)
             (q/stroke-weight 1)
             (q/background 240)
             0)
    :update (fn [tick] (inc tick))
    :draw (fn [tick]
            (when (< tick 16)
              (q/translate
                (+ 100 (* (/ (- w 100) 4) (mod tick 4)))
                (+ 100 (* (/ (- h 100) 4) (Math.floor (/ tick 4)))))
              (q/rotate (/ Math/PI 2))
              (let [shape (nth terms (* 2(Math.pow tick 2)))]
                (doseq [vertex shape] (turtle vertex)))))))


(defn draw-sketch []
  (timers/immediate
    #(sketch (dom/$ "canvas") 1000 1000)))


(defn init
  "Called on page load."
  []
  (enable-console-print!)
  (println "on-load"))


(draw-sketch)