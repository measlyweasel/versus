(ns handler
  (:use compojure.core)
  (:use [ring.middleware.json :only [wrap-json-params wrap-json-response]])
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [mongo :as mongo]
            [clojure.data.json :as json]
            [ring.util.response :as resp]))

(defroutes api-routes
           (GET "/" [] "Hello World")

           (GET "/tournaments" [] (json/write-str (mongo/getTournaments)))
           (GET "tournaments/:tournID" [tournId] "SPECIFIC TOURNAMENT")
           (POST "/tournaments" {tournament :params} (if-not (.getError (mongo/createTournament tournament)) resp/created))
           (DELETE "/tournaments/:tournId" [tournId] "I SHOULD DELETE")
           (PUT "/tournaments/:tournId" [tournId] "I SHOULD UPDATE")
           (POST "/tournaments/:tournId/vote" [tournId vote] "VOTE")

           (GET "/tournaments/:tournId/contenders" [tournId] "LIST OF CONTENDERS")
           (GET "/tournaments/:tournId/contenders/:contId" [tournId contId] "CONTENDER INFO")
           (POST "/tournaments/:tournId/contenders" [tournId] "ADD A CONTENDER")
           (DELETE "/tournaments/:tournId/contenders/:contId" [tournId contId] "DELETE A CONTENDER")
           (PUT "/tournaments/:tournId/contenders/:contId" [tournId contId] "I SHOULD UPDATE")


           (route/not-found "Not Found")
           )

(defroutes main-routes
           (context "/api" [] api-routes)
           (GET "/" [] (resp/resource-response "index.html" {:root "public"}))
           (route/resources "/")
           (route/not-found "Not Found")
           )

(def app (-> (handler/site main-routes) wrap-json-params wrap-json-response))

