(ns hgp.cljito.mocking-bird
  (:require [clojure.walk :refer :all]
            [clojure.pprint :refer :all]
            [hgp.cljito.real-fun-checkers :refer :all])
  (:import (clojure.lang ISeq)))

(defn any-boolean? [value] (boolean? value))
(defn any-byte? [value] (= (type value) java.lang.Byte))
(defn any-char? [value] (char? value))
(defn any-collection? [value] (= (type value) clojure.lang.ISeq))
(defn any-double? [value] (double? value))
(defn any-float? [value] (float? value))
(defn any-int? [value] (int? value))
(defn any-list? [value] (list? value))
(defn any-long? [value] (= (type value) java.lang.Long))
(defn any-map? [value] (map? value))

;; Meta data
(defrecord CustomMeta [c-stat-key c-stat-val])
(defrecord FunMetata [function-name
                      return-type
                      argument-types
                      custom-meta])

;; Flow of control runtime

(defrecord FunCalls [function-name
                     argument-values
                     return-value])

;; Function Mocking
(defrecord Attribute [pred-fun])
(defrecord FunAttributes [])
(defrecord WhenAttributes [attribute-list])

(defrecord Rule [function-name
                 action
                 arg-value
                 when-clause])

(def mock-meta (atom []))

(def mock-control-flow (atom []))

(def rule-vect (atom []))

(defmacro real-fun-checker [fun-name]
  `(let [real-fun# (resolve (var ~fun-name))]
     (if real-fun#
       real-fun#
       (throw (Exception. (str "No such function: " '~fun-name))))))

(defmacro when-> [function action val & args]
  `(if (not= (alength ~args)
            (get-fun-meta-args-count ~function))
    (throw
      (Exception.
        "Arguments do not match predicate per argument count"))
    (swap! rule-vect conj
          (Rule. ~function ~action
                 val
                 ~@args))))

(defn return-val [value]
  value)
(defn throw-ex [excp]
  (throw excp))

(defn do-nothing [dummy]
  'nothing)



(defn find-action-rule-for-fun [func-name]
  (first (filter #(= func-name (:function-name %)) @rule-vect)))

(defn find-meta-for-fun [func-name]
  (first (filter #(= func-name (:function-name %)) @mock-meta)))

(defn collect-meta [func-name & args]
  (if (= (find-meta-for-fun func-name) nil)
    (do
      (swap! mock-meta conj (FunMetata. func-name
                                        (type
                                          (apply func-name args))
                                        (map type args)
                                        nil)))))

(defn collect-flow-calls [func-name & args]
  (do
    (swap! mock-control-flow conj (FunCalls. func-name
                                             args
                                             (apply func-name args)))
    ))


(defn filter-action [func-name & args]
  ~@(let [rule (find-action-rule-for-fun func-name)]
    (if (not (nil? rule))
      (let [when-clause (:when-clause rule)
            the-args# (:arg-values rule)]
        (loop [the-clauses when-clause
               result Boolean/TRUE]
          (if (not (empty? the-clauses))
            (let [res (and result (apply (first the-clauses)
                                         args))]
              (recur (rest the-clauses) res))
            (if result
              (let [the-action (:action rule)
                    ]
                (the-action the-args#)
              ((var ~func-name) args))
            )
        ) ))
      ((var ~func-name) args)
  )))


(defmacro fun-mock-call [fun-name & args]
  `(do
    (collect-meta ~fun-name ~@args)
    (collect-flow-calls ~fun-name ~@args)
    (filter-action ~fun-name ~@args)
    ))

(defn retrieve-fun-names-vect [arguments]
  (if (and (vector? (first arguments))
           (vector? (second arguments)))
    (let [fun-names (map (fn [name] ~name) (first arguments))
          args (map (fn [name] ~name) (second arguments))
          body ~@(rest (rest (rest arguments)))]
      [fun-names args body])
    [[] [] ()]))

(defmacro dbg [body]
  `(let [x# ~body]
     (println "dbg:" '~body "=" x#)
     x#))

(defmacro fun-mock [& arguments]
  (let [[fun-names# args# body#] (retrieve-fun-names-vect ~@arguments)]
    ~@(with-redefs ~@(map (fn [name]
                        [name
                              ~@{:body (fun-mock-call name  args# )}]))
       fun-names#) ~@body#)
    )