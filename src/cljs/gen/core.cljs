(ns gen.core
  (:require [clojure.spec.alpha :as s]
            [clojure.test.check.generators :as gen]))


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


(defn init []

  (log (s/valid? ::rule (seq "F"))
       (s/valid? ::rule (seq "-F"))
       (s/valid? ::rule (seq "[F]"))
       (s/valid? ::rule (seq "F[-F]"))
       (s/valid? ::rule (seq "F[-F[+FF]]")))
  ;; => true true true true true

  )


(log (apply str
            (gen/generate (s/gen ::rule) 4)))
;; e.g. "FFF[[-]]"
;; e.g. "-F[F-+]"
;; e.g. "FF[[[[F]]F+][[+++]FF][F][F++]]F"
;; e.g. "F[F[[+][-+-][[++F]--FF]]++]+-"

(log (s/conform ::rule (seq "F[-F]")))
;; => [[:char "F"]
;;     [:group {:push "["
;;              :children [[:op "-"] [:char "F"]]
;;              :pop "]"}]]