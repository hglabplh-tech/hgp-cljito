(defproject hgp.cljito/mock-fn-hgp "0.2.4-SNAPSHOT"
  :description "Mockito wrapper for Clojure"
  :url "https://github.com/shaolang/cljito"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :profiles {:dev {:dependencies [[metosin/testit             "0.4.0"]
                                  [org.clojure/clojure        "1.10.1"]
                                  [org.mockito/mockito-core   "5.12.0"]
                                  [org.mockito/mockito-all    "2.0.2-beta"]
                                  [org.mockito/mockito-junit-jupiter "5.12.0"]
                                  [org.powermock/powermock-api-mockito2 "2.0.9"]]}
             :5.12.0  {:dependencies [[org.mockito/mockito-core "5.12.0"]]}
             :5.12.0-jup  {:dependencies [[org.mockito/mockito-junit-jupiter   "5.12.0"]]}
             :2.02-beta   {:dependencies [[org.mockito/mockito-all  "2.0.2-beta"]]}}

  :aliases {"all" ["with-profile"
                   "dev:dev,5.12.0:dev,2.02-beta:dev,5.12.0-jup:dev"]}

  :scm {:name "git"
        :url  "https://github.com/shaolang/cljito"}
  )