(ns examples.lindenmayer.data-test
  (:require-macros [cljs.test :refer [deftest testing is async]])
  (:require [examples.lindenmayer.data]
            [cljs.test]))


(deftest generates-lindenmayer-trees-with-string-input-and-rules
  (is (= (examples.lindenmayer.data/generate "F" "F+F[-F]" 2)
         "F+F[-F]+F+F[-F][-F+F[-F]]")))
