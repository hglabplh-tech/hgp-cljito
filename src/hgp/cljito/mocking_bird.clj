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
  `(swap! rule-vect conj
          (Rule. ~function ~action ~val [~@args]))
  )

(defn return-val [value]
  value)
(defn throw-ex [excp]
  (throw excp))

(defn do-nothing [dummy]
  'nothing)

(println (macroexpand-all
           '(when-> testit return-val 5 any-boolean? any-int? any-byte?)))
(println (macroexpand-all
          '(when-> testit throw-ex (Exception. "blah")
                    any-boolean? any-int? any-byte?)))
(println (macroexpand-all
           '(when-> testit do-nothing 'dummy
                    any-boolean? any-int? any-byte?)))

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


(defn filter-action [fun-name & args]
  (let [rule (find-action-rule-for-fun fun-name)]
    (if (not (nil? rule))
      (let [when-clause (:when-clause rule)]
        (loop [the-clauses when-clause
               result Boolean/TRUE]
          (if (not (empty? the-clauses))
            (let [res (and result (apply (fn [arg]
                                           ((first the-clauses) arg))
                                         args))]
              (recur (rest the-clauses) res))
            (if result
              (let [the-action (:action rule)
                    the-args (:arg-values rule)]
                (the-action the-args))
              `((var fun-name) args))
            ))
        )
    `((var fun-name) args)
  )))


(defn fun-mock-call [fun-name & args]
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
    `(with-redefs #(map (fn [~name]
                        (str  ~name " `{:body (fun-mock-call" ~name " args#)}"))
       fun-names#) body#)))



(defn i-am-a-fake [a b c]
  (* a b c))
(defn test-west [a b]
  (* a b))

(defn test-east [a b]
  (- a b))

(println (i-am-a-fake 8 9 4))
(println (macroexpand-all
           '(when-> i-am-a-fake return-val 5 any-int? any-int? any-int?)))

(def myfun  '(fun-mock [i-am-a-fake] [12 9 3]
                                     (fn [& arguments]
                                       (println arguments))))

(println (dbg (fun-mock [i-am-a-fake] [12 9 3]
                        (fn [& arguments]
                          (println arguments)))))
(println (macroexpand myfun))
(println (macroexpand-1 myfun))
(println (macroexpand-all myfun))
(println (i-am-a-fake 5 6 7))