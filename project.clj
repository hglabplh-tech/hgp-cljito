(defproject hgp.cljito/mock-fn-hgp "0.2.4-SNAPSHOT"
  :description "Mockito wrapper for Clojure"
  :url "https://github.com/shaolang/cljito"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[metosin/testit             "0.4.0"]
                                  [org.clojure/clojure "1.11.1"]
                                  [org.clojure/core.async "1.6.681"]
                                  [de.active-group/active-clojure "0.42.2"]
                                  [de.active-group/active-data "0.2.0-SNAPSHOT"]
                                  [org.mockito/mockito-core   "5.12.0"]
                                  [org.mockito/mockito-all    "2.0.2-beta"]
                                  [org.mockito/mockito-junit-jupiter "5.12.0"]
                                  [org.powermock/powermock-api-mockito2 "2.0.9"]]


  :aliases {"all" ["with-profile"
                   "dev:dev,5.12.0:dev,2.02-beta:dev,5.12.0-jup:dev"]}

  :scm {:name "git"
        :url  "https://github.com/shaolang/cljito"}

  :source-paths ["src/main/clj"]
  :java-source-paths ["src/main/java"]                      ; Java source is stored separately.
  :test-paths ["test/clj"]
  :resource-paths ["resource"]
  )