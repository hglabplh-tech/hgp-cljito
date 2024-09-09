(ns hgp.cljito.spy-and-mock.real-fun-checkers
  (:refer-clojure :exclude [ defn fn])
  (:require [clojure.walk :refer :all]
            [clojure.reflect :as refl]
            [clojure.pprint :refer :all]
            [active.data.realm :as realm]
            [active.data.realm :as realm]
            [active.data.realm.attach :refer :all]
            [active.data.realm.internal.record-meta :as act-meta]
            [schema.spec.core :refer :all]
            [schema.core :as schema]))
(def to-spy :spy-key)
(def to-mock :mock-key)
(defmacro get-fun-meta [funname]
  `(meta (var ~funname)))

(clojure.core/defn get-active-meta [all-meta-data gen-meta-data]
  (let [record-meta-data (get all-meta-data act-meta/record-realm-meta-key)
        fields-meta-data (get all-meta-data act-meta/fields-realm-map-meta-key)
        schema-meta-data (get all-meta-data :schema)
        result-meta (conj  {:schema schema-meta-data act-meta/record-realm-meta-key record-meta-data
                            act-meta/fields-realm-map-meta-key fields-meta-data} gen-meta-data)]
    (println  result-meta)
    result-meta

    )

  )
;;; implement it with that logic slightly changed
;; from active-data and in active data it works the thing
;; with using constantly seems to me a very good idea


(defmacro transfer-fun-meta
  "Macro to set the Meta of the mocked function to a mock"
  [generated-fun orig-fun]
  `(do (alter-meta! (var ~generated-fun)
                    (constantly
                      (assoc (get-active-meta (meta (var ~orig-fun))
                                              (meta (var ~generated-fun)))
                        :mock-type :fun)))
       (var ~generated-fun)))

(defmacro add-meta-mock
  "Macro to set the Meta of the mocked function to a mock"
  [fun-name]
  `(do (alter-meta! (var ~fun-name)
                    (constantly
                      (assoc (meta (var ~fun-name))
                        to-mock ~fun-name)))
       (var ~fun-name)))

(defmacro add-meta-spy
  "Macro to set the Meta of the mocked function to a mock"
  [fun-name]
  `(do (alter-meta! (var ~fun-name)
                    (constantly
                      (assoc (meta (var ~fun-name))
                        to-spy ~fun-name)))
       (var ~fun-name)))


(defmacro get-fun-meta-val-by-key [fun-name the-tag]
  `(get (meta (var ~fun-name))
        ~the-tag))

(defmacro get-fun-meta-args [the-fun-name]
  `(get-fun-meta-val-by-key ~the-fun-name
                            :arglists))

(defmacro get-fun-meta-args-count [the-fun-name]
  `(count (first (get-fun-meta-val-by-key ~the-fun-name
                                          :arglists))))

(defmacro get-fun-meta-ns [the-fun-name]
  `(get-fun-meta-val-by-key ~the-fun-name
                            :ns))

(defmacro get-fun-meta-ns-sym [the-fun-name]
  `(symbol (.toString (get-fun-meta-val-by-key ~the-fun-name
                                               :ns))))

(defmacro get-fun-meta-name [the-fun-name]
  `(get-fun-meta-val-by-key ~the-fun-name
                            :name))

(defmacro get-fun-meta-line [the-fun-name]
  `(get-fun-meta-val-by-key ~the-fun-name
                            :line))

(defmacro get-fun-meta-col [the-fun-name]
  `(get-fun-meta-val-by-key ~the-fun-name
                            :column))

(clojure.core/defn parse-base-schema [input]
  (let [ret-val (first input)
        params (first (rest input))]
    [ret-val params]
    ))

(defmacro get-fun-meta-schema [the-fun-name]
  `(let [result# (get-fun-meta-val-by-key ~the-fun-name
                                          :schema)
         cooked-result# (parse-base-schema result#)
         ]
     cooked-result#
     ))



