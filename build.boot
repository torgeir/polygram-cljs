(set-env!
  :source-paths #{"src/cljs"}
  :resource-paths #{"html"}
  :dependencies '[[org.clojure/clojure "1.9.0"]
                  [org.clojure/clojurescript "1.9.946"]
                  [org.clojure/core.async "0.4.474"]

                  [quil "2.6.0"]

                  ;; the cljs task to compile cljs
                  [adzerk/boot-cljs "2.1.4"]

                  ;; the serve task to serve target/ folder
                  [pandeiro/boot-http "0.8.3"]
                  [org.clojure/tools.nrepl "0.2.13"] ;; required by boot-http

                  ;; the reload task to reload the browser
                  [adzerk/boot-reload "0.5.2"]

                  ;; the cljs-repl task to start a repl
                  [adzerk/boot-cljs-repl "0.3.3"]
                  [com.cemerick/piggieback "0.2.1"] ;; required by cljs-repl
                  [weasel "0.7.0"] ;; required by cljs-repl

                  ;; for spec generators
                  [org.clojure/test.check "0.9.0"]

                  ;; for improved chrome dev tools
                  [powerlaces/boot-cljs-devtools "0.2.0"]
                  [binaryage/devtools "0.9.9"]
                  ])


(require '[adzerk.boot-cljs :refer [cljs]]
         '[pandeiro.boot-http :refer [serve]]
         '[adzerk.boot-reload :refer [reload]]
         '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
         '[powerlaces.boot-cljs-devtools :refer [cljs-devtools]])


(deftask dev []
  (comp
    (serve :dir "target")
    (watch)
    (cljs-devtools)
    (reload)
    (cljs-repl)
    (cljs)
    (target)))