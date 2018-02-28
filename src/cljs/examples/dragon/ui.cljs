(ns examples.dragon.ui
  (:require [polygram.log :refer [log]]
            [polygram.dom :as dom]
            [polygram.timers :as timers]
            [polygram.core :refer [grow step-index]]
            [quil.core :as q]
            [quil.middleware :as m]
            [cljs.core.async :as async :include-macros true]))


(defn turtle
  "Pull operations from chan and draw a lindenmayer tree."
  [canvas w h chan]
  (q/sketch
    :host canvas
    :size [w h]
    :middleware [m/fun-mode]
    :setup (fn []
             (q/frame-rate 0)
             (q/background 255)
             {:first true})
    :update (fn [s]
              (let [op (async/poll! chan)]
                (-> s
                  (assoc :first false)
                  (assoc :op op))))
    :draw (fn [{:keys [op first]}]
            (q/pop-matrix)
            (when first
              (q/translate (/ w 3) (/ h 2.5))
              (q/rotate Math/PI))
            (when op
              (cond
                (number? op) (do
                               (q/stroke-weight (q/random 1))
                               (q/stroke (q/random 200 255)
                                         (q/random 80 90)
                                         (q/random 40 50))
                               (let [l op]
                                 (q/line 0 0 0 l)
                                 (q/translate 0 l)))
                (= "R" op)   (q/rotate (/ Math/PI 2))
                (= "L" op)   (q/rotate (/ Math/PI -2)))
              (q/push-matrix)))))


(defn draw
  "Draw a lindenmayer tree to canvas."
  [canvas w h]

  (let [axiom [5]
        pred  (constantly true)
        rule  (fn [unit _ term]
                (concat [unit "L"]
                        (->> term
                          (reverse)
                          (map #(condp = %
                                  "R" "L"
                                  "L" "R"
                                  %)))))]
    (->> (grow axiom
               [[pred rule]]
               (step-index #(-> %1 count dec)))
      (take 15)
      (last)
      (async/to-chan)
      (turtle canvas w h))))


(defn draw-it []
  (timers/immediate
    #(draw (dom/$ "canvas")
           (-> js/document .-documentElement .-clientWidth)
           (-> js/document .-documentElement .-clientHeight))))


(defn init
  "Called on page load."
  []
  (println "on-load"))

(draw-it)
