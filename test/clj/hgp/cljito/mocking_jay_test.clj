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


    (call-cond-> i-am-a-fake-fun-store
                 :when
                 :any-boolean?-key :<-
                 [[:any-int?-key :$] [:any-int?-key :$] [:any-int?-key :$]
                   [:any-set-of?-key :$ integer?]]
                 [return-val 5]
                 :else
                 [do-nothing])

    (mock i-am-a-fake-fun-store)
    (i-am-a-fake-fun-store 7 8 9)))


(run-tests)


