(ns lindenmayer.data
  (:require [lindenmayer.ui]
            [clojure.string :as string]))


(def cool-trees
  "Some cool lindenmayer trees."
  ["F+F[-F]"
   "FF[+F][--FF][-F+F]"
   "F[-FF[+F]]F[+F[+F]]"
   "F[++F[-F]]F[-FF[F]]"
   "F[-F[-F++F]][+F[--F]]F"])


(defn generate
  "Create a string representing the tree to draw."
  ([axiom rule] (generate axiom rule 2 axiom))
  ([axiom rule steps] (generate axiom rule steps axiom))
  ([axiom rule steps acc]
   (if (= 0 steps)
     acc
     (let [new-acc (string/replace acc axiom rule)]
       (recur axiom rule (dec steps) new-acc)))))