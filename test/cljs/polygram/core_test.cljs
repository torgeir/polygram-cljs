(ns polygram.core-test
  (:require [cljs.test :refer [deftest testing is async] :include-macros true]
            [examples.lindenmayer.data]
            [polygram.random :as random]
            [polygram.core :as polygram]
            [clojure.string]))


(deftest does-not-repeat-numbers-in-safe-random []
  (let [sr (random/rand-no-repeat 2)]
    (is (= #{0 1 nil}
           (set (map #(sr) (range 4)))))))


(deftest finds-applicable-rules []
  (let [axiom       ["a" 2]
        number-rule [number? inc]
        rules       [number-rule]]

    (is (empty? (polygram/applicable-rules axiom 0 rules)))

    (is (= (polygram/applicable-rules axiom 1 rules)
           (list inc)))))


(deftest applies-rule []
  (is (= (polygram/apply-rule inc ["a" 42] 1)
         43)))


(deftest rule-predicate-application-provides-context []
  (let [results (atom [])
        axiom   ["a" "b" "c"]]
    (->> (polygram/grow axiom [[(fn [& args]
                                  (swap! results conj args)) identity]])
      (take 1)
      (last))
    (is (= @results [["a" 0 axiom]
                     ["b" 1 axiom]
                     ["c" 2 axiom]]))))


(deftest rule-application-provides-context []
  (let [results (atom [])
        axiom   ["a" "b" "c"]]
    (->> (polygram/grow axiom [[string? (fn [& args]
                                          (swap! results conj args))]])
      (take 1)
      (last))
    (is (= @results [["a" 0 axiom]
                     ["b" 1 axiom]
                     ["c" 2 axiom]]))))


(deftest applies-rule-for-all-units-in-one-step []
  (let [number-rule [number? (fn [v] ["L" 1 "R" v "R" 1 "L"])]]
    (is (= (->> (polygram/grow [42 "R" 42] [number-rule])
             (take 2)
             (first))
           ["L" 1 "R" 42 "R" 1 "L" "R" "L" 1 "R" 42 "R" 1 "L"]))))


(deftest applies-rule-for-unit-at-index-given-by-stepper []
  (let [number-rule [number? inc]
        terms       (polygram/grow ["R" 42 "R"]
                                   [number-rule]
                                   (polygram/step-index (constantly 1)))]

    (is (= (->> terms (take 1) (first))
           ["R" 43 "R"]))

    (is (= (->> terms (take 2) (last))
           ["R" 44 "R"]))))


(deftest can-implement-lindenmayer-tree-generator
  (let [f?          #(= "F" %)
        replacement (constantly (seq "F+F[-F]"))
        f-rule      [f? replacement]
        terms       (polygram/grow ["F"] [f-rule])]

    (is (= (->> terms (take 1) (last) (clojure.string/join ""))
           "F+F[-F]"))

    (is (= (->> terms (take 2) (last) (clojure.string/join ""))
           "F+F[-F]+F+F[-F][-F+F[-F]]"))))

