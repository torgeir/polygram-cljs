(ns gen.core
  (:require [clojure.string :as string]
            [clojure.spec.alpha :as s]
            [clojure.test.check.generators :as gen]
            [cljs.core.async :as async :include-macros true]
            [quil.core :as q]
            [quil.middleware :as m]))

(defn log [& args]
  (apply (.-log js/console) args))


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
(comment (log "Some generated rules:" (take 3 rules)))
;; => ("FFF[[-]]" "-F[F-+]" "F[F[[+][-+-][[++F]--FF]]++]+-")


(comment (log "Parsed F[-F]:" (parse-rule "F[-F]")))
;; => [[:char "F"]
;;     [:group {:push "["
;;              :children [[:op "-"] [:char "F"]]
;;              :pop "]"}]]


(defn tree
  "Create a string representing the tree to draw."
  ([axiom rule] (tree axiom rule 2 axiom))
  ([axiom rule steps acc]
   (if (= 0 steps)
     acc
     (let [new-acc (string/replace acc axiom rule)]
       (recur axiom rule (dec steps) new-acc)))))


(def tree-chan (async/to-chan "F-[FF]FF"))


(defn create-element [type]
  (.createElement js/document type))


(defn append [parent el]
  (.appendChild parent el))


(defn prepend [parent el]
  (.insertBefore parent el (.-firstChild parent)))


(defn setup
  []
  (q/frame-rate 10)
  (q/background 200)
  {:x 0
   :y 0
   :first true})

(defn update-fn
  [s]
  (-> s
    (assoc :first false)
    (assoc :op (async/poll! tree-chan))))

(defn draw
  [s]
  (q/translate 100 200)
  (q/rotate Math/PI)
  (when-let [op (:op s)]
    (log op)
    (condp = op
      "F" (do
            (q/line 0 0 0 20)
            (q/translate 0 20))
      "-" (do
            (q/rotate (- Math/PI 10)))
      "[" (q/push-matrix)
      "]" (q/pop-matrix)
      nil)))

(defn render []

  (def body (.-body js/document))

  (when-let [canvas (.querySelector js/document "canvas")]
    (.removeChild body canvas))

  (def canvas (create-element "canvas"))
  (set! (.-id canvas) "w00t")
  (prepend body canvas)

  (q/sketch
    :host "w00t"
    :size [200 200]
    :setup setup
    :draw draw
    :update update-fn
    :middleware [m/fun-mode])
  )

(defn init
  "Called on page load."
  []
  (.setTimeout js/window #(render) 1000))

