(ns handler-test
  (:import (java.io File))
  (:require [clojure.test :refer :all]
            [handler :refer :all]
            [clojure.data.json :as json])
  (:use ring.mock.request)
  (:use [mongo :only [init-db createTournament]])
  (:use [monger.core :only [connect! drop-db]])
  )

(defn db-setup-fixture [f]
  (connect!)
  (drop-db "handlerTest")
  (init-db "handlerTest")
  (f)
  )

(use-fixtures :once db-setup-fixture)

(deftest test-api-route
  (testing "api route"
    (let [response (app (request :get "/api/"))]
      (is (= (:status response) 200))
      (is (= (:body response) "Hello World")))))

(deftest create-tournament
  (testing "create tournament"
    (let [response (app (-> (request :post "/api/tournaments")
                            (body (json/write-str {:_id "blah" :description "bang"}))
                            (content-type "application/json")))]
      (is (= (response :status) 201))                       ; 201 is HTTP success Created
      )))

(deftest find-tournament
  (testing "get one tournament"
    ;given
    (createTournament {:_id "thingie"})

    (let [response (app (request :get "/api/tournaments/thingie"))]
      (is (= (response :status) 200))
      (is (= (response :body) (json/write-str {:_id "thingie"})))
      )))

(deftest delete-tournament
  (testing "delete a tournament via api"
    ;given
    (createTournament {:_id "whatever"})
    (is (monger.collection/any? mongo/tournamentCollectionName {:_id "whatever"}))

    (let [response (app (request :delete "/api/tournaments/whatever"))]
      (is (= (response :status) 204)))

    (is (not (monger.collection/any? mongo/tournamentCollectionName {:_id "whatever"})))
    ))

(deftest update-tournament
  (testing "update a tournament via the api"
    ;given
    (def updatedTournament {:_id "updater" :description "something"})
    (createTournament {:_id "updater" :description "none"})

    ;when
    (let [response (app (-> (request :put "/api/tournaments/updater")
                            (body (json/write-str updatedTournament))
                            (content-type "application/json")))]
      (is (= (response :status) 204)))

    (is (= (monger.collection/find-map-by-id mongo/tournamentCollectionName "updater")
           updatedTournament)))

  (testing "updates with bad ids in body respect the url id"
    (let [response (app (-> (request :put "/api/tournaments/updater")
                            (body (json/write-str {:_id "BAD" :description "something else"}))
                            (content-type "application/json")))]
      (is (= (response :status) 204)))
    (is (= (monger.collection/find-map-by-id mongo/tournamentCollectionName "updater")
           {:_id "updater" :description "something else"}))
    )
  )


(deftest tournament-list
  (testing "list tournaments"
    (let [response (app (request :get "/api/tournaments"))]
      (is (= (response :status) 200))
      )))

(deftest invalid-api-request
  (testing "not-found route"
    (let [response (app (request :get "/api/invalid"))]
      (is (= (:status response) 404)))))


(deftest test-main-index
  (testing "main route"
    (let [response (app (request :get "/"))]
      (is (= (:status response) 200))
      (is (instance? File (:body response)))
      (is (= (.getName (:body response)) "index.html")))))

(deftest invalid-main-request
  (testing "main not-found route"
    (let [response (app (request :get "/invalid"))]
      (is (= (:status response) 404)))))

(run-tests)
