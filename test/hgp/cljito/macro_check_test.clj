(ns hgp.cljito.macro-check-test
  (:require [clojure.test :refer :all]
            [clojure.walk :refer :all]
            [clojure.pprint :refer :all]
            [hgp.cljito.real-fun-checkers :refer :all]
            [hgp.cljito.mocking-jay :refer :all])
  (:import (clojure.lang Symbol Namespace)))

(defn m-test-fun-one [one two three four five]
  (* five (+ three (- one four) (/ one two)))
  )

(deftest fun-fun-mock-macro-test
  (testing "Test the fun-mock macro"
    (is (= (macroexpand-all
                            '(fun-mock m-test-fun-one [12 9 3]
                                        (println "arguments"))
             ) 7))))