(ns hgp.cljito.real-fun-checkers
  (:require   [clojure.walk :refer :all]
              [clojure.pprint :refer :all]))

(defmacro get-fun-meta [funname]
   `(meta (var ~funname)))

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