(ns gen.log)


(defn log [& args]
  (apply (.-log js/console) args))

