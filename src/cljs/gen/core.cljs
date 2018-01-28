(ns gen.core
  (:require [clojure.spec.alpha :as s]
            [clojure.test.check.generators :as gen]
            [quil.core :as q]))


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


(defn init
  "Called on page load."
  []
  (log (s/valid? ::rule (seq "F"))
       (s/valid? ::rule (seq "-F"))
       (s/valid? ::rule (seq "[F]"))
       (s/valid? ::rule (seq "F[-F]"))
       (s/valid? ::rule (seq "F[-F[+FF]]"))))


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
(log "Some generated rules:" (take 3 rules))
;; => ("FFF[[-]]" "-F[F-+]" "F[F[[+][-+-][[++F]--FF]]++]+-")


(log "Parsed F[-F]:" (parse-rule "F[-F]"))
;; => [[:char "F"]
;;     [:group {:push "["
;;              :children [[:op "-"] [:char "F"]]
;;              :pop "]"}]]

(defn create-element [type]
  (.createElement js/document type))


(defn append [parent el]
  (.appendChild parent el))


(def body (.-body js/document))


(when-let [old-sketch (.querySelector js/document "#single-sketch")]
  (.removeChild body old-sketch))


(def canvas (create-element "canvas"))
(set! (.-id canvas) "single-sketch")
(append body canvas)


(defn setup
  []
  (q/frame-rate 4)
  (q/background 200))


(defn draw
  []
  (q/stroke (q/random 255))
  (q/stroke-weight (q/random 10))
  (q/fill (q/random 255))

  (let [diam (q/random 100)
        x    (q/random (q/width))
        y    (q/random (q/height))]
    (q/ellipse x y diam diam)))


(q/sketch
  :host canvas
  :size [200 200]
  :setup setup
  :draw draw)