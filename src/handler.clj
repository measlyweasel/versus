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

           (GET "/tournaments" [] (resp/response (mongo/getAllTournaments)))

           (GET "/tournaments/:tournId" [tournId] (resp/response (mongo/getTournament tournId)))

           (POST "/tournaments" {tournament :params}
                 (let [mongoResponse (mongo/createTournament tournament)]
                   (if-not (.getError mongoResponse)
                     (resp/created (str "/tournaments/" (tournament :_id)))
                     (resp/status (resp/response "") 500)))
                 )
           (DELETE "/tournaments/:tournId" [tournId]
                   (let [mongoResponse (mongo/deleteTournament tournId)]
                     (if-not (.getError mongoResponse)
                       (resp/status (resp/response "") 204)
                       (resp/status (resp/response "") 500))
                     ))
           (PUT "/tournaments/:_id" {tournament :params} (let [mongoResponse (mongo/updateTournament tournament)]
                                                           (if-not (.getError mongoResponse)
                                                             (resp/status (resp/response "") 204)
                                                             (resp/status (resp/response "") 500))))


           (POST "/tournaments/:tournId/vote" {vote :params routeParams :route-params} (let [mongoResponse (mongo/vote (routeParams :tournId) vote)]
                                                               (if-not (.getError mongoResponse)
                                                                 (resp/status (resp/response "") 200)
                                                                 (resp/status (resp/response "") 500))))

           (POST "/tournaments/:tournId/contenders" {contender :params} (let [mongoResponse (mongo/addContender (contender :tournId) (contender :name))]
                                                                          (if-not (.getError mongoResponse)
                                                                            (resp/created (str "/tournaments/" (contender :tournId) "/contenders/" (contender :name)))
                                                                            (resp/status (resp/response "") 500))))

           (route/not-found "Not Found")
           )

(defroutes main-routes
           (context "/api" [] api-routes)
           (GET "/" [] (resp/resource-response "index.html" {:root "public"}))
           (route/resources "/")
           (route/not-found "Not Found")
           )

(def app (-> (handler/site main-routes) wrap-json-params wrap-json-response))

