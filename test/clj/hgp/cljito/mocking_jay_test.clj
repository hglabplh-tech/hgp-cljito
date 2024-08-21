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

(clojure.core/defn i-am-a-fake-fun-store [msg a b]
  (let [res (* a b)]
    (println msg res)
    (boolean res)))

(deftest test-extended.mock
  (testing "the extended mock"
    (println (type i-am-a-fake-fun))
    (println (type i-am-a-fake-fun-store))

    (when-> hgp.cljito.mocking-jay-test/i-am-a-fake-fun-store return-val 5
            :any-int?-key :any-int?-key :any-int?-key)
    ;;(mock i-am-a-fake-fun)
    (alter-var-root (var  i-am-a-fake-fun-store)  (clojure.core/fn [f]
                                       (clojure.core/fn [msg a b]
                                         (mock-call i-am-a-fake-fun-store))))
    (is (= (i-am-a-fake-fun-store 7 8 9) 5))))


(run-tests)


