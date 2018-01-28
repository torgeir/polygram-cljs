(ns gen.core)

(comment
  (:require [clojure.spec.alpha :as s]
            [clojure.test.check.generators :as gen]))

(defn init []
  (println {:woot 42
            :it   :works}))