(ns polygram.macros)

(defmacro unless
  [pred a]
  `(when (not ~pred) ~a))