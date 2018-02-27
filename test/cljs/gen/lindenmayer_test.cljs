(ns gen.lindenmayer-test
  (:require [cljs.test :refer [deftest testing is async] :include-macros true]
            [examples.lindenmayer.data]
            [gen.random :as random]
            [gen.lindenmayer :as lm]
            [clojure.string]))


(deftest does-not-repeat-numbers-in-safe-random []
  (let [sr (random/rand-no-repeat 2)]
    (is (= #{0 1 nil}
           (set (map #(sr) (range 4)))))))


(deftest finds-applicable-rules []
  (let [units       ["a" 2]
        number-rule [number? inc]
        rules       [number-rule]]

    (is (empty? (lm/applicable-rules units 0 rules)))

    (is (= (lm/applicable-rules units 1 rules)
           (list inc)))))


(deftest applies-rule []
  (is (= (lm/apply-rule inc ["a" 42] 1)
         43)))


(deftest rule-predicate-application-provides-context []
  (let [res   (atom [])
        axiom ["a" "b" "c"]]
    (->> (lm/grow axiom [[(fn [& args]
                            (swap! res conj args)) identity]])
      (take 1)
      (last))
    (is (= @res [["a" 0 axiom]
                 ["b" 1 axiom]
                 ["c" 2 axiom]]))))


(deftest rule-application-provides-context []
  (let [res   (atom [])
        axiom ["a" "b" "c"]]
    (->> (lm/grow axiom [[string? (fn [& args]
                                    (swap! res conj args))]])
      (take 1)
      (last))
    (is (= @res [["a" 0 axiom]
                 ["b" 1 axiom]
                 ["c" 2 axiom]]))))


(deftest applies-rule-for-all-units-in-one-step []
  (let [number-rule [number? (fn [v] ["L" 1 "R" v "R" 1 "L"])]]
    (is (= (->> (lm/grow [42 "R" 42] [number-rule])
             (take 2)
             (first))
           ["L" 1 "R" 42 "R" 1 "L" "R" "L" 1 "R" 42 "R" 1 "L"]))))


(deftest applies-rule-for-unit-at-index-given-by-stepper []
  (let [number-rule [number? inc]
        terms       (lm/grow ["R" 42 "R"]
                             [number-rule]
                             (lm/step-index #(constantly 1)))]

    (is (= (->> terms (take 1) (first))
           ["R" 43 "R"]))

    (is (= (->> terms (take 2) (last))
           ["R" 44 "R"]))))


(deftest can-implement-lindenmayer-tree-generator
  (let [f?          #(= "F" %)
        replacement (constantly (seq "F+F[-F]"))
        f-rule      [f? replacement]
        terms       (lm/grow ["F"] [f-rule])]

    (is (= (->> terms (take 1) (last) (clojure.string/join ""))
           "F+F[-F]"))

    (is (= (->> terms (take 2) (last) (clojure.string/join ""))
           "F+F[-F]+F+F[-F][-F+F[-F]]"))))

