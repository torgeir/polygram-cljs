(ns examples.lindenmayer.data
  (:require [clojure.string]
            [gen.lindenmayer]))


(def cool-trees
  "Some cool lindenmayer trees."
  ["F+F[-F]"
   "FF[+F][--FF][-F+F]"
   "F[-FF[+F]]F[+F[+F]]"
   "F[++F[-F]]F[-FF[F]]"
   "F[-F[-F++F]][+F[--F]]F"])


(defn generate
  "Create a string representing the tree to draw."
  ([axiom rule steps]
   (let [f?          (partial = "F")
         replacement (constantly (seq rule))
         f-rule      [f? replacement]]
     (->> (gen.lindenmayer/rule-applier (clojure.string/split axiom "") [f-rule])
       (take steps)
       (last)
       (clojure.string/join "")))))