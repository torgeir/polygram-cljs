(ns gen.core
  (:require [gen.dom :as dom]
            [gen.timers :as timers]
            [lindenmayer.art]))


(defn init
  "Called on page load."
  [])


(timers/immediate #(lindenmayer.art/draw
                     (dom/$ "canvas")
                     (-> js/document .-documentElement .-clientWidth)
                     (-> js/document .-documentElement .-clientHeight)))