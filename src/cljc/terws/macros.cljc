(ns terws.macros)

(defmacro unless
  [pred a]
  `(when (not ~pred) ~a))