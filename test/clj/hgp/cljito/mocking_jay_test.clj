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
    (println (macroexpand-all '(mock-call i-am-a-fake-fun)))
    (when-> hgp.cljito.mocking-jay-test/i-am-a-fake-fun return-val 5
            :any-int?-key :any-int?-key :any-int?-key)
    (let [fun (mock-call i-am-a-fake-fun)]
      (println (meta fun))
   (with-redefs [i-am-a-fake-fun fun]
    (let [let-res (i-am-a-fake-fun 7 8 9)]
      (println "Hello")
      (println let-res)
    (is (= (i-am-a-fake-fun 7 8 9) 6)))
    )
    )))

(deftest test-extended.mock.total
  (testing "the extended mock - with mock macro"
    (println (get-fun-meta-args i-am-a-fake-fun))
    (println (macroexpand-all '(mock-call i-am-a-fake-fun)))
    (when-> hgp.cljito.mocking-jay-test/i-am-a-fake-fun return-val 5
            :any-int?-key :any-int?-key :any-int?-key)
    (let [fun (mock-call i-am-a-fake-fun)]
      (println (meta fun))
      (mock i-am-a-fake-fun
        (let [let-res (i-am-a-fake-fun 7 8 9)]
          (println let-res)
          (is (= (i-am-a-fake-fun 7 8 9) 6)))
        )
      )))

(run-tests)


