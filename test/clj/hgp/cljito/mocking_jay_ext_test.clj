(ns hgp.cljito.mocking-jay-ext-test
  (:refer-clojure :exclude [def defn fn])
  (:require [clojure.test :refer :all]
            [active.data.realm :as realm]
            [active.data.realm.attach :refer :all]
            [hgp.cljito.mocking-jay :refer :all]
            [active.data.realm.schema :as realm-schema]
            [schema.core :as schema]))

(schema/set-fn-validation! (boolean 1))

;; :schema (=> Bool Str Num Num),
;; :active.data.realm.attach/realm #active.data.realm.internal.records/function-realm
;; {description function (string, number, number) -> boolean
(defn test-add :- realm/boolean
  [msg :- realm/string a :- realm/number b :- realm/number]
  (let [res (* a b)]
    (println msg res)
  (boolean res)))


(deftest the-active-data-base-test
  (testing "One basic test for mocking with active data"
    (when-> test-add return-val 5
            :any-int?-key :any-int?-key :any-int?-key)
    (is (= (fun-mock-call test-add  7 8 9) 5))
    ))

(clojure.core/defn thats-me [msg]  (println "What :" msg) )
(clojure.core/defn thats-not-me [msg]  (println "Why :" msg) )
(deftest over-write-name
  (testing "hey whats up"

    (thats-me "blubber")
    (thats-not-me "blubber")
    (alter-var-root (var thats-me )  (clojure.core/fn [f]
                                       (clojure.core/fn [msg]
                                                 (thats-not-me msg))))
    (thats-me "huhu")



    ))

(run-tests)
