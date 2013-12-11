(defproject versus-backend "0.1.0-SNAPSHOT"
  :description "FIXME: write description"

  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.6"]
                 [com.novemberain/monger "1.5.0"]
                 [org.clojure/math.combinatorics "0.0.7"]
                 [org.clojure/data.json "0.2.3"]]

  :plugins [[lein-ring "0.8.8"]]

  :ring {:handler versus-backend.handler/app}

  :profiles {:dev {:dependencies  [[javax.servlet/servlet-api "2.5"]
                                  [ring-mock "0.1.5"]]}})
