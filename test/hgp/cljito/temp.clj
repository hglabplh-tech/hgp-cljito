(ns hgp.cljito.temp
  (:require [clojure.test :refer :all]))
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
(println (i-am-a-fake 8 9 4))

(println (macroexpand-all
           '(when-> i-am-a-fake return-val 5 any-int? any-int? any-int?)))
(println (macroexpand-all '(fun-mock-call i-am-a-fake 7 8 9)))

(when-> i-am-a-fake return-val 5 any-int? any-int? any-int?)

(println (fun-mock-call i-am-a-fake 7 8 9))

(println (macroexpand-all
           '(when-> testit return-val 5 any-boolean? any-int? any-byte?)))
(println (macroexpand-all
           '(when-> testit throw-ex (Exception. "blah")
                    any-boolean? any-int? any-byte?)))
(println (macroexpand-all
           '(when-> testit do-nothing 'dummy
                    any-boolean? any-int? any-byte?)))

