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
;;    * A set of context-dependent rules over A (see description below)
;;    * A number indicating the number of rule applications.
;;    
;;    A rule over an alphabet A is either:
;;    * Context-free: A 2-tuple consisting of input (a member of A) and output (a sequence over A).
;;    * Context-dependent: A 2-tuple consisting of an A-check and output (a sequence over A).
;;    
;;    An A-check is a function that takes a sequence over A and an index, and returns a boolean value.
;;
;;    Usage:
;;    (gen/dol-gen "a" [["a", ["a","b"]], ["b", ["a","a"]]] 4)
;;    (gen/dol-gen "F" [["F", (string/split "FF[+F][--FF][-F+F" #"")]] 4)

(defn apply-rule-on-atom
    "Returns rule output if its input matches atom"
    [rules atom]

    (let [matching-rule (first (filter (fn [[i o]] (= atom i)) rules))]
        (if (nil? matching-rule)
            atom
            (second matching-rule))))

(defn apply-rules
    "Applies rules on whole sequence once"
    [seq rules]
    (vec (flatten (map (partial apply-rule-on-atom rules) seq))))

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
