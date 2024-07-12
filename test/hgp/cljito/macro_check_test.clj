(ns hgp.cljito.macro-check-test
  (:require [clojure.test :refer :all]
            [clojure.walk :refer :all]
            [clojure.pprint :refer :all]
            [hgp.cljito.real-fun-checkers :refer :all]
            [hgp.cljito.mocking-bird :refer :all])
  (:import (clojure.lang Symbol Namespace)))

(defn m-test-fun-one [one two three four five]
  (* five (+ three (- one four) (/ one two)))
  )

(deftest when->macro-tests
  (testing "the when-> macro with different constellations"
    (is (= (macroexpand-all '(when-> m-test-fun-one throw-ex (Exception. "dummy")
                                     any-int? any-int? any-int? any-int? any-int?)) 9))
    ))
(deftest fun-mock-call-macro-test
  (testing "The fun-mock-call macro"
    (is (= (let [expanded (macroexpand-all
                            (fun-mock-call m-test-fun-one 8 9 67 54 11))]
             (pprint expanded)
             expanded)  7))
    ))

(deftest fun-fun-mock-macro-test
  (testing "Test the fun-mock macro"
    (is (= (let [expanded (macroexpand-all
                            (fun-mock m-test-fun-one [12 9 3]
                                      (fn  [& arguments]
                                        (println arguments))))]
             (pprint expanded)) 7))
    ))

(run-tests)
