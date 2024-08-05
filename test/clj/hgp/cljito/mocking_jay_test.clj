(ns hgp.cljito.mocking-jay-test
  (:refer-clojure :exclude [def defn fn])
  (:require [clojure.test :refer :all]
            [clojure.walk :refer :all]
            [clojure.pprint :refer :all]
            [active.data.realm :as realm]
            [active.data.realm.attach :refer :all]
            [hgp.cljito.real-fun-checkers :refer :all]
            [hgp.cljito.mocking-jay :refer :all])
  (:import (clojure.lang Symbol Namespace)))

(defn i-am-a-fake-fun :- realm/boolean
  [msg :- realm/string a :- realm/number b :- realm/number]
  (let [res (* a b)]
    (println msg res)
    (boolean res)))

(deftest test-extended.mock
  (testing "the extended mock"
    (println (get-fun-meta-args i-am-a-fake-fun))
    (when-> hgp.cljito.mocking-jay-test/i-am-a-fake-fun return-val 5
            :any-int?-key :any-int?-key :any-int?-key)
   (with-redefs [i-am-a-fake-fun (mock-call i-am-a-fake-fun)]
    (is (= (i-am-a-fake-fun 7 8 9) 6)))))


