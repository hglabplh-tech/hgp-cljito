(ns hgp.cljito.mocking-jay
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
;; create mapping for functions
;; Meta data
(def type-predicate-map
  {:any-boolean?-key    any-boolean?
   :any-byte?-key       any-byte?
   :any-char?-key       any-char?
   :any-collection?-key any-collection?
   :any-double?-key     any-double?
   :any-float?-key      any-float?
   :any-int?-key        any-int?
   :any-list?-key       any-list?
   :any-long?-key       any-long?
   :any-map?-key        any-map?})

(defn get-type-pred-by-key [key]
  (get type-predicate-map key))

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

(defmacro make-fn [m]
  `(fn [& args#]
     (eval
       (cons '~m args#))))

(def mock-meta (atom []))

(def mock-control-flow (atom []))

(def rule-vect (atom []))

(defmacro real-fun-checker [fun-name]
  `(let [real-fun# (resolve (var ~fun-name))]
     (if real-fun#
       real-fun#
       (throw (Exception. (str "No such function: " '~fun-name))))))

(defn when-> [fun action val & args]
  (swap! rule-vect conj
         (Rule. fun
                action
                val
                (apply list args))))

(defn return-val [value]
  (println "Mock ret val called")
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

(defn collect-flow-calls [func-name ret-value & args]
  (do
    (swap! mock-control-flow conj (FunCalls. func-name
                                             args
                                             ret-value))
    ))

;; rewrite this fun does not fit to be called from a macro :-(
(defn filter-action [func-name & args]
  (let [rule (find-action-rule-for-fun func-name)]
    (if (not (nil? rule))
      (let [the-args (:arg-value rule)]
        (loop [the-clauses (:when-clause rule)
               condition (boolean 1)]
          (if (not (empty? the-clauses))
            (do
              (println (first the-clauses))
              (let [res (and condition (apply (make-fn and)
                                              (map (get-type-pred-by-key
                                                     (first the-clauses))
                                                   args)))]
                (recur (rest the-clauses) res)))
            (if condition
              (let [the-action (:action rule)
                    result (the-action the-args)]
                (println "mock called")
                result)
              (do
                (println "real called")
                (apply func-name args))
              ))
          ))
      (do
        (println "fallback")
      (apply func-name args)
      ))))


(defn fun-mock-call [fun-name & args]
  (apply collect-meta fun-name args)
  (let [result (apply filter-action fun-name args)]
    (apply collect-flow-calls fun-name result args)
    result))

(defn retrieve-fun-names-vect [arguments]
  (if (and (vector? (first arguments))
           (vector? (second arguments)))
    (let [args (map (fn [name] ~name) (first arguments))
          body(rest arguments)]
      [args body])
    [[] [] ()]))

(defmacro dbg [body]
  `(let [x# ~body]
     (println "dbg:" '~body "=" x#)
     x#))

(defn fun-mock [func body & args]
    `(clojure.core/with-redefs [~func  (fun-mock-call
                                                ~func ~args)]
         ~body
  ))