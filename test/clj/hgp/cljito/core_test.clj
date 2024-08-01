(ns cljito.core-test
  (:require [cljito.core :refer :all]
            [clojure.test :refer [deftest]]
            [testit.core :refer [fact facts]])
  (:import [java.util ArrayList List]
           [org.mockito Mockito]))

(deftest mock-tests
  (fact "mocks are Mockito mocks"
    (.isMock (Mockito/mockingDetails (mock List))) => true)

  (facts "mocks are stubbed, just like Java's"
    (.get (when-> (mock List)
                  (.get 0)
                  (.thenReturn "it works"))
          0)
    => "it works"

    (.get (when-> (mock List)
                  (.get 0)
                  (.thenThrow RuntimeException))
          0)
    =throws=> RuntimeException

    (let [mock-list (when-> (mock List)
                            (.get 0)
                            (.thenReturn "first")
                            (.thenReturn "second"))]
      [(.get mock-list 0) (.get mock-list 0)])
    => ["first" "second"]))


(deftest spy-tests
  (fact "spies are Mockito spy"
    (-> (Mockito/mockingDetails (spy (ArrayList.))) (.isSpy))
    => true))


(deftest verify-support-tests
  (let [never-cleared (mock List)]
    (facts
      (verify-> never-cleared (.clear))
      =throws=> AssertionError

      (verify-> never-cleared never (.clear))
      => nil))


  (let [cleared-once (mock List)]
    (.clear cleared-once)

    (facts
      (verify-> cleared-once (.clear))
      => nil

      (verify-> cleared-once 1 (.clear))
      => nil

      (verify-> cleared-once 2 (.clear))
      =throws=> AssertionError)))


(deftest argument-matcher-support-tests
  (facts "argument matchers support"
    (.get (when-> (mock List)
                  (.get (any-int))
                  (.thenReturn "argument matchers works"))
          12345)
    => "argument matchers works"))


(deftest do-*-stubbing-tests
  (facts "support for do* stubbings"
    (.get (do-return "it works"
            (.when (mock List))
            (.get 0))
          0)
    => "it works"

    (.clear (do-throw (throwables (UnsupportedOperationException.))
              (.when (mock List))
              (.clear)))
    =throws=> UnsupportedOperationException

    (let [spied-list (spy (ArrayList.))]
      (.add spied-list "still around")
      (do-nothing (.when spied-list) (.clear))
      (.clear spied-list)
      (.get spied-list 0)) => "still around"))
