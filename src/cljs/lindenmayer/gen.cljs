(ns lindenmayer.gen)

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
;;
;;   Usage:
;;   (gen/dol-gen "a" [["a", ["a","b"]], ["b", ["a","a"]]] 4)
;;   (gen/dol-gen "F" [["F", (seq "FF[+F][--FF][-F+F")]] 4)

(defn apply-rule-on-atom
  "Returns rule output if its input matches atom"
  [rules atom]
  (if-let [matching-rule (first (filter (fn [[i o]] (= atom i)) rules))]
    (second matching-rule)
    atom))

(defn apply-rules
  "Applies rules on whole sequence once"
  [s rules]
  (vec (flatten (map #(apply-rule-on-atom rules %) s))))

(defn apply-rules-repeatedly
  "Applies rules on whole sequence a given number of times"
  [input rules steps]
  (if (= steps 0)
    input
    (recur (apply-rules input rules) rules (dec steps))))

(defn dol-gen
  "Applies deterministic, context-free rules on single axiom a given number of times"
  [axiom rules steps]
  (apply-rules-repeatedly axiom rules steps))

(def number-rule [number? (fn [val index arr]
                            (let [n (int (rand 10))]
                              ["L" n "R" val "R" n "L"]))])

(defn safe-random
  "Random non-repeating number from 0 to n."
  [n]
  (let [a (atom (range n))]
    (fn []
      (let [shuffled (shuffle @a)]
        (reset! a (rest shuffled))
        (first shuffled)))))


(defn applicable-rules
  "Find applicable rules for the unit at index of units."
  [units index rules]
  (->> rules
    (filter (fn [[pred fn]]
              (pred (units index))))
    (map second)))


(defn apply-rule
  "Apply `rule-fn` at location `index` of `units`."
  [rule-fn units index]
  (->> (-> index units rule-fn)
    (assoc units index)
    (flatten)
    (vec)))


(defn next-application
  "Run a random applicable rule at an index of units."
  ([units rules] (next-application units rules (safe-random (count units))))
  ([units rules index-fn]
   (let [index (index-fn)
         rule-fns (applicable-rules units index rules)]
     (if (empty? rule-fns)
       (recur units rules index-fn)
       (apply-rule (rand-nth rule-fns) units index)))))


(defn rule-applier
  "Lazy seq of iterations of applied rules to units."
  [units rules]
  (lazy-seq
    (let [new-units (next-application units rules)]
      (cons new-units
            (rule-applier new-units rules)))))

(->> (rule-applier [1 "R" 2 "R" 3 "R" 4] [number-rule])
  (drop 100)
  (take 1)
  (first)
  (println)) ;; check the devtools log


