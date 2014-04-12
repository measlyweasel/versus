(ns versus-backend.handler
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [versus-backend.mongo :as mongo]
            [clojure.data.json :as json]))

(defroutes api-routes
           (GET "/" [] "Hello World")

           (GET "/tournament" [] (json/write-str (mongo/getTournaments)))
           (POST "/tournament/:name" [name] (mongo/createTournament name))
           (route/not-found "Not Found")
           )

(defroutes main-routes
           (context "/api" [] api-routes)
          ; (route/resources "/")
           (route/not-found "Not Found")
           )

(def app
  (handler/site main-routes))

