(ns hgp.cljito.mocking-jay-test
  (:require [clojure.test :refer :all]
            [clojure.walk :refer :all]
            [clojure.pprint :refer :all]
            [hgp.cljito.real-fun-checkers :refer :all]
            [hgp.cljito.mocking-jay :refer :all])
  (:import (clojure.lang Symbol Namespace)))

(defn i-am-a-fake-fun [a b c]
  (* a b c))
(when-> i-am-a-fake-fun return-val 5
        :any-int?-key :any-int?-key :any-int?-key)

(deftest test-simple.mock
  (testing "the simple mock"
  (is (= (fun-mock-call i-am-a-fake-fun  7 8 9) 5))
  ))

(deftest test-extended.mock
  (testing "the extended mock"
   (fun-mock i-am-a-fake-fun
                     (is (= (i-am-a-fake-fun 7 8 9) 5))
                     7 8 9)
    ))


