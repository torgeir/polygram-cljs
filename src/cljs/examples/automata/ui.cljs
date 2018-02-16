(ns examples.automata.ui
  (:require [gen.dom :as dom]
            [gen.timers :as timers]
            [gen.lindenmayer :as lm]
            [quil.core :as q]
            [quil.middleware :as m]
            [cljs.core.async :as async :include-macros true]))

(def width 500)
(def cell-size 2)
(def rule-number (int (rand 256)))

(def axiom (vec (concat (take (int (/ width 2)) (repeat 0)) [1] (take (int (/ width 2)) (repeat 0)))))

(defn pad-with-zero [length s]
  (loop [s s] (if (< (count s) length) (recur (str "0" s)) s)))
  
(def rule [
  (fn [val index vals] (and (> index 0) (< index (- width 1))))
  (fn [val index vals]
    (let [n (+ 
      (vals (dec index))
      (* (vals index) 2)
      (* (vals (inc index)) 4))]
        (int ((vec (pad-with-zero 8 (.toString rule-number 2))) n))))])

(def results (lm/rule-applier axiom [rule]))

(defn turtle [line]
  (loop [line line]
    (when (not (empty? line))
    (let [b (first line)]
      (if (= b 0) (q/fill 0) (q/fill 255))
      (q/rect 0 0 cell-size cell-size)
      (q/translate cell-size 0)
      (recur (rest line))))))
        
(defn sketch
  [canvas w h]
  (q/sketch
    :host canvas
    :size [w h]
    :middleware [m/fun-mode]
    :setup (fn []
             (q/frame-rate 0)
             (q/no-stroke)
             (q/background 240)
             (println rule-number)
             0)
    :update (fn [tick] (inc tick))
    :draw (fn [tick]
      (when (< tick width)
        (q/translate 0 (* tick cell-size))
        (turtle (nth results tick))))))

(defn draw-sketch []
  (timers/immediate
    #(sketch (dom/$ "canvas") 1000 1000)))

(defn init
  "Called on page load."
  []
  (enable-console-print!)
  (draw-sketch))