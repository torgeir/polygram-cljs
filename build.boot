(set-env!
  :source-paths #{"src/cljs"}
  :resource-paths #{"html"}
  :dependencies '[[org.clojure/clojure "1.9.0"]
                  [org.clojure/clojurescript "1.9.946"]
                  [org.clojure/core.async "0.4.474"]

                  ;; the cljs task to compile cljs
                  [adzerk/boot-cljs "2.1.4"]

                  ;; the serve task to serve target/ folder
                  [pandeiro/boot-http "0.8.3"]
                  [org.clojure/tools.nrepl "0.2.13"] ;; required by boot-http

                  ;; the reload task to reload the browser
                  [adzerk/boot-reload "0.5.2"]

                  ])


(require '[adzerk.boot-cljs :refer [cljs]]
         '[pandeiro.boot-http :refer [serve]]
         '[adzerk.boot-reload :refer [reload]])

(deftask dev []
  (comp
    (serve :dir "target")
    (watch)
    (reload)
    (cljs)
    (target)))