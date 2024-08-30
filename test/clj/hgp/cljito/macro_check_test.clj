(ns hgp.cljito.macro-check-test
  (:refer-clojure :exclude [def defn fn])
  (:require [clojure.test :refer :all]
            [clojure.walk :refer :all]
            [clojure.pprint :refer :all]
            [active.data.realm :as realm]
            [active.data.realm.attach :refer :all]
            [active.data.realm.internal.record-meta :as act-meta]
            [schema.spec.core :refer :all]
            [hgp.cljito.real-fun-checkers :refer :all]
            [hgp.cljito.mocking-jay :refer :all])
  (:import (clojure.lang Symbol Namespace)))

(defn i-am-a-fake-fun :- realm/boolean
  [msg :- realm/string a :- realm/number b :- realm/number]
  (let [res (* a b)]
    (println msg res)
    (boolean res)))

(clojure.core/defn i-am-a-fake-fun-store [msg a b]
  (let [res (* a b)]
    (println msg res)
    (boolean res)))

(clojure.core/defn m-test-fun-one [one two three four five]
  (* five (+ three (- one four) (/ one two)))
  )

(deftest fun-fun-mock-macro-test
  (testing "Test the fun-mock macro"
    (println (type i-am-a-fake-fun))
    (println (type i-am-a-fake-fun-store))
   ))