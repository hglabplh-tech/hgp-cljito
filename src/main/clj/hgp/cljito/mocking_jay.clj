(ns hgp.cljito.mocking-jay

  (:require [clojure.walk :refer :all]
            [clojure.pprint :refer :all]
            [hgp.cljito.real-fun-checkers :refer :all])

  (:import (hgp.cljito DeepMock)
           (clojure.lang IFn Var)))


(clojure.core/defn any-boolean? [value] (boolean? value))
(clojure.core/defn any-byte? [value] (= (type value) java.lang.Byte))
(clojure.core/defn any-char? [value] (char? value))
(clojure.core/defn any-collection? [value] (= (type value) clojure.lang.ISeq))
(clojure.core/defn any-double? [value] (double? value))
(clojure.core/defn any-float? [value] (float? value))
(clojure.core/defn any-int? [value] (int? value))
(clojure.core/defn any-list? [value] (list? value))
(clojure.core/defn any-long? [value] (= (type value) java.lang.Long))
(clojure.core/defn any-map? [value] (map? value))

(clojure.core/defn get-set-of? [pred]
  (clojure.core/fn [value]
    (and [(set? value) (map pred value)])
    ))
((get-set-of? integer?) '(3 4 5 67 8))
(clojure.core/defn any-object? [value] (= (type value) java.lang.Object))
(clojure.core/defn any-set? [value] (set? value))
(clojure.core/defn any-set-of? [value pred] (set?))
(clojure.core/defn any-short? [value] (= (type value) java.lang.Short))
(clojure.core/defn any-string? [value] (string? value))
(clojure.core/defn any-vararg? [value] ())

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
   :any-map?-key        any-map?
   :any-object?-key     any-object?
   :any-set?-key        any-set?
   :any-set-of?-key     any-set-of?
   :any-short?-key      any-short?
   :any-string?-key     any-string?
   :any-vararg?-key     any-vararg?
   })

(clojure.core/defn get-type-pred-by-key [key]
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
(defrecord ActiveDataCustom [var-names var-optional?])
(defrecord Mocker [function-name])
(defrecord ArgCond [fun params])
(defrecord WhenClauses [ret-val-type-pred param-cond])
(defrecord Rule [function-name
                 when-clauses
                 when-action
                 else-action])



(defmacro make-fn [m]
  `(clojure.core/fn [& args#]
     (eval
       (cons '~m args#))))

(def mock-meta (atom []))

(def mock-control-flow (atom []))

(def rule-vect (atom []))

(def mocked-bindings (atom {}))


;;(call-cond-> i-am-a-fake-fun-store
;                 :when
;                 :any-boolean?-key :<-
;                 [[:any-int?-key :$] [:any-int?-key :$] [:any-int?-key :$]
;                   [:any-set-of?-key :$ integer?]]
;                 [return-val 5]
;                 :else
;                 [do-nothing])
(clojure.core/defn call-cond-> [fun
                                cond?
                                ret-val-pred
                                delim
                                args-vect
                                when-action-vect
                                else?
                                else-action-vect]
  (let [ret-val-pred ret-val-pred
        delim?-ok (= delim :<-)
        args-list (map
                    (clojure.core/fn [arg-element]
                      (let [arg-pred (first arg-element)
                            parameters (rest arg-element)]
                        (clojure.core/fn [value]
                          ;; has to be rewritten because also
                          ;; zero parms are possible (should work !!!)
                          (if (not= (count parameters) 0)
                            (if ((= :$ (get parameters 0))
                                 (= (count parameters) 2))
                              (apply arg-pred
                                     (list value
                                           (get parameters 1)))
                              (arg-pred value))
                            (arg-pred))
                          ))) args-vect)
        when-action (ArgCond. (first when-action-vect)
                              (rest when-action-vect))
        else-action (if else?
                      (ArgCond. (first else-action-vect)
                                (rest else-action-vect))
                      nil)]
    (if delim?-ok
      (swap! rule-vect conj
             (Rule. `~fun
                    (WhenClauses. ret-val-pred args-list)
                    when-action
                    else-action
                    ))
      (throw (java.lang.IllegalArgumentException.
               "macro syntax !!! ")))))

(clojure.core/defn return-val [value]
  (println "Mock ret val called")
  value)

(clojure.core/defn throw-ex [excp]
  (throw excp))

(clojure.core/defn do-nothing [dummy]
  'nothing)

(clojure.core/defn find-action-rule-for-fun [func-name]
  (first (filter #(= func-name (:function-name %)) @rule-vect)))

(clojure.core/defn find-meta-for-fun [func-name]
  (first (filter #(= func-name (:function-name %)) @mock-meta)))

(clojure.core/defn find-mocked-fun [func-name]
(first (filter #(= func-name  (:function-name %)) @mocked-bindings)))


(clojure.core/defn parse-arg-types [arg-type-defs]
  (let [id (first (first arg-type-defs))
        descriptors (first (second (first arg-type-defs)))
        arg-type-seq (vec (map (clojure.core/fn [val]
                                 (get val :schema)) descriptors))
        arg-opt?-seq (vec (map (clojure.core/fn [val]
                                 (get val :optional?)) descriptors))
        arg-name-seq (vec (map (clojure.core/fn [val]
                                 (get val :name)) descriptors))]
    (pprint id)
    (pprint arg-type-seq)
    (pprint arg-opt?-seq)
    (pprint arg-name-seq)
    (let [fun-result {:names-vect     arg-name-seq
                      :types-vect     arg-type-seq
                      :optional?-vect arg-opt?-seq}]
      (pprint fun-result)
      fun-result)
    ))

(clojure.core/defn parse-meta-to-map [schema-val]
  (let [return-type (second (first schema-val))
        arg-info-map (parse-arg-types (rest schema-val))]
    [{:return-type return-type} {:arg-info-map arg-info-map}]
    ))

(clojure.core/defn collect-meta-active-data [func-name & args]
  `(if (= (find-meta-for-fun ~func-name) nil)
     `(let [schema-here? (not= (get-fun-meta-schema ~func-name) nil)]
        (if schema-here?
          (let [schema-val## (get-fun-meta-schema ~func-name)
                schema-val-map## (parse-meta-to-map schema-val##)]
            (swap! mock-meta conj (FunMetata. ~func-name
                                              (get (first schema-val-map##)
                                                   :return-type)
                                              (get (get (second schema-val-map##)
                                                        :arg-info-map)
                                                   :types-vect)
                                              (ActiveDataCustom.
                                                (get (get (second schema-val-map##)
                                                          :arg-info-map)
                                                     :names-vect)
                                                (get (get (second schema-val-map##)
                                                          :arg-info-map)
                                                     :optional?-vect))))
            )
          (swap! mock-meta conj (FunMetata. func-name
                                            nil
                                            (map type args)
                                            nil))))))


(clojure.core/defn collect-flow-calls [func-name ret-value & args]
  (do
    (swap! mock-control-flow conj (FunCalls. func-name
                                             args
                                             ret-value))
    ))


;; rewrite this fun does not fit to be called from a macro :-(
(clojure.core/defn filter-action [func-name & args]
  (let [rule (find-action-rule-for-fun func-name)
        fun-meta-rec (find-meta-for-fun func-name)]
    (if (not (nil? rule))
      (let [the-return-type-pred (:ret-val-type-pred
                                   (:when-clauses rule))]
        (loop [the-clauses (:args-list (:when-clauses rule))
               condition (the-return-type-pred (:return-type fun-meta-rec))]
          (if (not (empty? the-clauses))
            (do
              (println (first the-clauses))
              (let [res (and condition (apply (make-fn and)
                                              (map (get-type-pred-by-key
                                                     (first the-clauses))
                                                   args)))]
                (recur (rest the-clauses) res)))
            (if condition
              (let [the-action (:when-action rule)
                    result ((:fun the-action)
                            (:params the-action))]
                (println "when-action called with ret: "
                         result)
                result)
              (if (not (nil? (:else-action rule)))
                (let [the-action (:else-action rule)
                      result (apply (:fun the-action)
                              (:params the-action))]
                  (println "else-action called with ret: "
                             result
                             ))
                (do
                  (println "real called")
                  'no-condition-fits
                  ))
              ))
          ))
      (do
        (println "fallback")
        'no-mock-logic
        ))))

(clojure.core/defn fun-mock-call
  [fun-name & args]
  `(apply collect-meta-active-data ~fun-name args)
  (let [fun-name# `~fun-name
        args# `~args
        result (apply filter-action fun-name# args#)]
    (apply collect-flow-calls fun-name# result args#)
    result))

(clojure.core/defn bind-root
  ""
  {:static true}
  [^clojure.lang.Var v f] (.bindRoot v f))


(defn- mocker-1
  [var-name]
    `(let [the-fun# ~var-name]
      (alter-var-root
         (var ~var-name)
         (constantly
           (clojure.core/fn [& args#]
            (fun-mock-call the-fun# args#)) ))))


(defmacro mock
  "Re-exports a bunch of names, copying metadata"
  [& names]
  `(do ~@(map mocker-1 names)))

(clojure.core/defn unmock [funs])

