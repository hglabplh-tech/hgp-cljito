(ns hgp.cljito.real-fun-checkers-ext-test
  (:refer-clojure :exclude [def defn fn])
  (:require [clojure.test :refer :all]
            [active.data.realm :as realm]
            [active.data.realm.attach :refer :all]
            [active.data.realm.schema :as realm-schema]
            [clojure.pprint :refer :all]
            [hgp.cljito.real-fun-checkers :refer :all]
            [hgp.cljito.mocking-jay :refer :all]
            [schema.spec.core :refer :all]))


;; :schema (=> Bool Str Num Num),
;; :active.data.realm.attach/realm #active.data.realm.internal.records/function-realm
;; {description function (string, number, number) -> boolean
(defn test-add :- realm/boolean
      [msg :- realm/string a :- realm/number b :- realm/number]
      (let [res (* a b)]
        (println msg res)
        (boolean res)))

(clojure.core/defn test-fun [name a b]
  (let [result (* a b)]
    (println name result)
    result))


(def test-fun-def
  (let []
  (println  (macroexpand-1 '(transfer-fun-meta
    test-fun
    test-add)))
  (transfer-fun-meta
    test-fun
    test-add))
  )


(deftest test-meta-of-fun-active-data

  (testing "the meta values of a function defn / fn
  for active-data extensions"
    (def test-result [[:output-schema
                       java.lang.Boolean]
                      [:input-schemas
                       [[#schema.core.One{:name      msg
                                          :optional? false
                                          :schema    java.lang.String}
                         #schema.core.One{:name      a
                                          :optional? false
                                          :schema    java.lang.Number}
                         #schema.core.One{:name      b
                                          :optional? false
                                          :schema    java.lang.Number}]]]])

    (pprint (rest
              (get-fun-meta-schema test-add)))

    (is (= (second (first (get-fun-meta-schema test-add)))
           java.lang.Boolean))

    (is (= (second
             (first
               (first
                 (first
                   (second
                     (first
                       (rest
                         (get-fun-meta-schema test-add))))))))
           java.lang.String))

    (is (= (second
             (first
               (second
                 (first
                   (second
                     (first
                       (rest
                         (get-fun-meta-schema test-add))))))))
           java.lang.Number))

    (is (= (second
             (first
               (first
                 (rest
                   (first
                     (second
                       (first
                         (rest
                           (get-fun-meta-schema test-add)))))))))
           java.lang.Number))
    ))




(deftest test-get-mock-with-meta
  (testing "the way to give original meta definitions to the mock"

    (println (type test-fun-def))
    (println  (meta test-fun-def))

    (is (= (test-fun "Hello result:" 7 2) 14))
    (is (= (test-fun-def "Hello result copy fun:" 7 2) 14))
    (is (= (second (first (get-fun-meta-schema test-add)))
           java.lang.Boolean))
    (is (= (second (first (get-fun-meta-schema-anonymous
                            (meta test-fun-def))))    java.lang.Boolean))
    ))


