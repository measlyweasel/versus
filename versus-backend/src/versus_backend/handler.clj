(ns versus-backend.handler
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [versus-backend.mongo :as mongo]
            [clojure.data.json :as json]))

(defroutes app-routes
           (GET "/" [] "Hello World")

           (GET "/tournaments" [] (json/write-str (mongo/getTournaments)))
           (POST "/tournaments/:name"  [name] (mongo/createTournament name))

           (route/resources "/")
           (route/not-found "Not Found")
           )

(def app
  (handler/site app-routes))

