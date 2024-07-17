(ns hgp.cljito.real-fun-checkers-ext-test
  (:refer-clojure :exclude [def defn])
  (:require [clojure.test :refer :all]
            [active.data.realm :as realm]
            [active.data.realm.attach :refer :all]
            [active.data.realm.schema :as realm-schema]
            [clojure.pprint :refer :all]
            [hgp.cljito.real-fun-checkers :refer :all]
            [schema.core :as schema]))


;; :schema (=> Bool Str Num Num),
;; :active.data.realm.attach/realm #active.data.realm.internal.records/function-realm
;; {description function (string, number, number) -> boolean
(defn test-add :- realm/boolean
      [msg :- realm/string a :- realm/number b :- realm/number]
      (let [res (* a b)]
        (println msg res)
        (boolean res)))

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

    (defn parse-arg-types :- realm/any [arg-type-defs :- realm/any]
          (let [id (first (first arg-type-defs))
                descriptors (first  (second (first arg-type-defs)))
                arg-type-seq (map (clojure.core/fn [val]
                                      (get val :schema)) descriptors)
                arg-opt?-seq (map (clojure.core/fn [val]
                                      (get val :optional?)) descriptors)
                arg-name-seq (map (clojure.core/fn [val]
                                      (get val :name)) descriptors)]
            (pprint arg-type-seq)
            (pprint arg-opt?-seq)
            (pprint arg-name-seq)
            [arg-name-seq arg-type-seq arg-opt?-seq]
            ))
    (parse-arg-types (rest (get-fun-meta-schema test-add)))

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
