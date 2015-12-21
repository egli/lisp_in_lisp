(defproject lisp_in_lisp "0.1.0-SNAPSHOT"
  :description "Demo code for Eval in Lisp"
  :url "http://example.com/FIXME"
  :license {:name "Creative Commons Attribution-ShareAlike 4.0 International"
            :url "http://creativecommons.org/licenses/by-sa/4.0/"}
  :dependencies [[org.clojure/clojure "1.7.0"]]
  :main ^:skip-aot lisp-in-lisp.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})

