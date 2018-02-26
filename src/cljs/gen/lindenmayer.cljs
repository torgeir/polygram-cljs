(ns gen.lindenmayer
  (:require [gen.random :as random]))

;;   Given alphabet A.
;;
;;   `D0L Generate` takes three arguments:
;;   * An axiom  (a member of A)
;;   * A set of context-free rules over A (see description below)
;;   * A number indicating the number of rule applications.
;;
;;   `D1L Generate` takes three arguments:
;;   * An axiom  (a member of A)
;;   * A set of context-dependent rules over A (see description below)
;;   * A number indicating the number of rule applications.
;;
;;   A rule over an alphabet A is either:
;;   * Context-free: A 2-tuple consisting of input (a member of A) and output (a sequence over A).
;;   * Context-dependent: A 2-tuple consisting of an A-check and output (a sequence over A).
;;
;;   An A-check is a function that takes a sequence over A and an index, and returns a boolean value.


(defn applicable-rules
  "Find applicable rules for the unit at index of the term."
  [term index rules]
  (->> rules
    (filter (fn [[pred fn]]
              (pred (term index) index term)))
    (map second)))


(defn apply-rule
  "Apply rule function at index of the term."
  [rule-fn term index]
  (rule-fn (term index) index term))


(defn step-all
  "Runs random applicable rule on each unit of the term."
  [term rules]
  (->> term
    (map-indexed (fn [index unit]
                   (let [rule-fns (applicable-rules term index rules)]
                     (if (empty? rule-fns)
                       unit
                       (apply-rule (rand-nth rule-fns) term index)))))
    (flatten)
    (vec)))


(defn step-one
  "Runs random applicable rule on index returned by marker-fn. Repeatedly calls
  the marker-fn until a rule can be applied for the unit at the index returned."
  [term rules marker-fn]
  (when-let [index (marker-fn)]
    (let [rule-fns (applicable-rules term index rules)]
      (if (empty? rule-fns)
        (recur term rules marker-fn)
        (->> (apply-rule (rand-nth rule-fns) term index)
          (assoc term index)
          (flatten)
          (vec))))))


(defn step-random
  "Runs random applicable rule (if any) at random index."
  [term rules]
  (step-one term rules (random/rand-no-repeat (count term))))


(defn grow
  "Lazily grows an axiom by repeatedly applying rules to units of the axiom."
  ([axiom rules] (grow axiom rules step-all))
  ([axiom rules step-fn]
   (lazy-seq
     (let [term (step-fn axiom rules)]
       (cons term
             (grow term rules step-fn))))))
