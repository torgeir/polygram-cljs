(ns gen.macros)

(defmacro unless
  [pred a]
  `(when (not ~pred) ~a))