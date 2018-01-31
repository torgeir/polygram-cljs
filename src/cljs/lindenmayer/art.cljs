(ns lindenmayer.art
  (:require [lindenmayer.ui]
            [lindenmayer.data]
            [cljs.core.async :as async :include-macros true]))


(defn draw
  "Draw a lindenmayer tree to canvas."
  [canvas w h]
  (let [data (lindenmayer.data/generate "F" (lindenmayer.data/cool-trees 0) 5)]
    (lindenmayer.ui/turtle canvas w h (async/to-chan data))))