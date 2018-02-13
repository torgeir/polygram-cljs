(set-env!
  :source-paths #{"src/cljs"}
  :resource-paths #{"html"}
  :dependencies '[[org.clojure/clojure "1.9.0"]
                  [org.clojure/clojurescript "1.9.946"]
                  [org.clojure/core.async "0.4.474"]

                  [quil "2.6.0"]

                  ;; for spec generators
                  [org.clojure/test.check "0.9.0"]

                  ;; the cljs task to compile cljs
                  [adzerk/boot-cljs "2.1.4" :scope "test"]

                  ;; the serve task to serve target/ folder
                  [pandeiro/boot-http "0.8.3" :scope "test"]
                  [org.clojure/tools.nrepl "0.2.13" :scope "test"] ;; required by boot-http

                  ;; the reload task to reload the browser
                  [adzerk/boot-reload "0.5.2" :scope "test"]

                  ;; the cljs-repl task to start a repl
                  [adzerk/boot-cljs-repl "0.3.3" :scope "test"]
                  [com.cemerick/piggieback "0.2.1" :scope "test"] ;; required by cljs-repl
                  [weasel "0.7.0" :scope "test"] ;; required by cljs-repl

                  ;; for improved chrome dev tools
                  [powerlaces/boot-cljs-devtools "0.2.0" :scope "test"]
                  [binaryage/devtools "0.9.9" :scope "test"]

                  ;; tests
                  [crisptrutski/boot-cljs-test "0.3.4" :scope "test"]
                  [doo "0.1.8" :scope "test"]])


(require '[adzerk.boot-cljs :refer [cljs]]
         '[pandeiro.boot-http :refer [serve]]
         '[adzerk.boot-reload :refer [reload]]
         '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
         '[powerlaces.boot-cljs-devtools :refer [cljs-devtools]]
         '[crisptrutski.boot-cljs-test :refer [test-cljs]])


(deftask dev
  "Development task, watch and hot reload cljs on change."
  []
  (comp
    (serve :dir "target")
    (watch)
    (cljs-devtools)
    (reload)
    (cljs-repl)
    (cljs)
    (target)))


(deftask testing
  "Add test source paths."
  []
  (merge-env! :source-paths #{"test/cljs"})
  identity)


(deftask test []
  "Runs tests."
  (comp (testing)
        (test-cljs)))


(deftask test-watch []
  "Watches for changes and auto-runs tests. Speaks the result."
  (comp (testing)
        (watch)
        (speak)
        (test-cljs)))