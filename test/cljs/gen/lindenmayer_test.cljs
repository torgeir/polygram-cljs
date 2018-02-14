(ns gen.lindenmayer-test
  (:require-macros [cljs.test :refer [deftest testing is async]])
  (:require [cljs.test]
            [gen.random :as random]
            [gen.lindenmayer :as lm]))


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


(deftest sequentially-applies-rule-for-all-units-in-one-step []
  (let [number-rule [number? (fn [v] ["L" 1 "R" v "R" 1 "L"])]]
    (is (= (->> (lm/rule-applier [42 "R" 42]
                                 [number-rule])
             (take 1)
             (first))
           ["L" 1 "R" 42 "R" 1 "L" "R" "L" 1 "R" 42 "R" 1 "L"]))))


(deftest applies-rule-for-unit-at-index-given-by-stepper []
  (let [number-rule  [number? inc]
        rule-applier (lm/rule-applier ["R" 42 "R"]
                                      [number-rule]
                                      #(lm/step %1 %2 (constantly 1)))]

    (is (= (->> rule-applier (take 1) (first))
           ["R" 43 "R"]))

    (is (= (->> rule-applier (take 2) (last))
           ["R" 44 "R"]))))