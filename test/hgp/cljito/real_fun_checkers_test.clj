(ns hgp.cljito.real-fun-checkers-test
  (:require [clojure.test :refer :all]
            [hgp.cljito.real-fun-checkers :refer :all])
  (:import (clojure.lang Symbol Namespace))
  )

(defn testit [i o p l]
  (+ i o p l))


(deftest test-meta-of-fun

  (testing "the meta values of a function defn / fn"
    (is (= (.toString (get-fun-meta-ns testit))
           "hgp.cljito.real-fun-checkers-test"))
    (is (= (get-fun-meta-ns-sym testit) 'hgp.cljito.real-fun-checkers-test))
    (is (= (get-fun-meta-name testit) 'testit))
    (is (= (type (first (first (get-fun-meta-args testit)))) Symbol))
    (is (= (first (get-fun-meta-args testit)) (list 'i 'o 'p 'l)))
    (is (= (get-fun-meta-args-count testit) 4))
    (is (= (get-fun-meta-line testit) 7))
    (is (= (get-fun-meta-col testit) 1))))
