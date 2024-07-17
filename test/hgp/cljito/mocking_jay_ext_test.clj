(ns hgp.cljito.mocking-jay-ext-test
  (:refer-clojure :exclude [def defn fn])
  (:require [clojure.test :refer :all]
            [active.data.realm :as realm]
            [active.data.realm.attach :refer :all]
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

(println (meta (var test-add)))
(println  "Result: " (test-add "Here I am with a result: " 0 2))
