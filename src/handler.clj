(ns handler
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [mongo :as mongo]
            [clojure.data.json :as json]
            [ring.util.response :as resp]))

(defroutes api-routes
           (GET "/" [] "Hello World")

           (GET "/tournament" [] (json/write-str (mongo/getTournaments)))
           (POST "/tournament/:name" [name] (mongo/createTournament name))
           (route/not-found "Not Found")
           )

(defroutes main-routes
           (context "/api" [] api-routes)
           (GET "/" [] (resp/resource-response "index.html" {:root "public"}))
           (route/resources "/")
           (route/not-found "Not Found")
           )

(def app
  (handler/site main-routes))

