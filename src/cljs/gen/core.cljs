(ns gen.core
  (:require [clojure.string :as string]
            [clojure.spec.alpha :as s]
            [clojure.test.check.generators :as gen]
            [cljs.core.async :as async :include-macros true]
            [quil.core :as q]
            [quil.middleware :as m]
            [gen.dom :as dom]))


(s/def ::alphabet #{"F"})


(s/def ::operator #{"-" "+"})


(s/def ::group (s/cat
                 :push     #{"["}
                 :children ::rule
                 :pop      #{"]"}))


(s/def ::rule (s/+
                (s/alt :op    ::operator
                       :char  ::alphabet
                       :group ::group)))


(defn create-rule
  "Create a rule of `size` using test.check."
  [size]
  (->> (gen/generate (s/gen ::rule) size)
    (apply str)))


(defn parse-rule
  "Parse a rule to data."
  [rule]
  (s/conform ::rule (seq rule)))


(def rules (repeatedly #(create-rule 4)))


(defn tree
  "Create a string representing the tree to draw."
  ([axiom rule] (tree axiom rule 2 axiom))
  ([axiom rule steps] (tree axiom rule steps axiom))
  ([axiom rule steps acc]
   (if (= 0 steps)
     acc
     (let [new-acc (string/replace acc axiom rule)]
       (recur axiom rule (dec steps) new-acc)))))

(comment
  (tree "F"
        "F+F[-F]"
        ;;"F[-F[-F++F]][+F[--F]]F"
        ;;"F[-FF[+F]]F[+F[+F]]"
        ;;"F[++F[-F]]F[-FF[F]]"
        ;;"FF[+F][--FF][-F+F]"
        4))

(defn render [canvas]
  (let [w (-> js/document .-documentElement .-clientWidth)
        h (-> js/document .-documentElement .-clientHeight)
        tree-chan (async/to-chan
                    ;;(tree "F" (create-rule 20) 2)
                    (tree "F" "FF[+F][--FF][-F+F]" 5)
                    ;;(tree "F" "F[-F][+F]F" 10)
                    )]
    (q/sketch
      :host canvas
      :size [w h]
      :middleware [m/fun-mode]
      :setup (fn []
               (q/frame-rate 1000)
               (q/background 255)
               {:first true
                :n 1})
      :update (fn [{:keys [n] :as s}]
                (let [op (async/poll! tree-chan)]
                  (-> s
                    (assoc :first false)
                    (assoc :op op)
                    (assoc :n (condp = op
                                "[" (* n 0.8)
                                "]" (/ n 0.8)
                                n)))))
      :draw (fn [{:keys [op n first]}]
              (q/pop-matrix)
              (when first
                (q/translate (/ w 2) (/ h 1.2))
                (q/rotate Math/PI))
              (when op
                (condp = op
                  "F" (do
                        (q/stroke-weight (q/random (* 5 n)))
                        (q/stroke (q/random 255))
                        (q/stroke (q/random 0 50)
                                  (q/random 50 200)
                                  (q/random 0 50))
                        (let [l (q/random 1 (* 20 n))]
                          (q/line 0 0 0 l)
                          (q/translate 0 l)))
                  "-" (q/rotate (rand -0.55))
                  "+" (q/rotate (rand 0.90))
                  "[" (q/push-matrix)
                  "]" (q/pop-matrix)
                  nil)
                (q/push-matrix))
              ))))

(defn init
  "Called on page load."
  []
  )


(.setTimeout js/window
             (fn []
               (render (dom/$ "canvas"))
               )
             0)