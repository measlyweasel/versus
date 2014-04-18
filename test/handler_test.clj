(ns handler-test
  (:import (java.io File))
  (:require [clojure.test :refer :all]
            [handler :refer :all]
            [clojure.data.json :as json])
  (:use ring.mock.request)
  (:use [mongo :only [init-db]])
  (:use [monger.core :only [connect! drop-db]])
  )

(defn db-setup-fixture [f]
  (connect!)
  (drop-db "handlerTest")
  (init-db "handlerTest")
  (f)
  )

(use-fixtures :once db-setup-fixture)

(deftest test-api
  (testing "api route"
    (let [response (app (request :get "/api/"))]
      (is (= (:status response) 200))
      (is (= (:body response) "Hello World"))))

  (testing "create tournament"
    (let [response (app (-> (request :post "/api/tournaments")
                            (body (json/write-str {:_id "blah" :description "bang"}))
                            (content-type "application/json")))]
      (is (= (response :status) 201)) ; 201 is HTTP success Created
      ))

  (testing "list tournaments"
    (let [response (app (request :get "/api/tournaments"))]
      (is (= (response :status) 200))
      ))

  (testing "not-found route"
    (let [response (app (request :get "/api/invalid"))]
      (is (= (:status response) 404))))

  (testing "not-found route"
    (let [response (app (request :get "/api/invalid"))]
      (is (= (:status response) 404))))
  )

(deftest test-main
  (testing "main route"
    (let [response (app (request :get "/"))]
      (is (= (:status response) 200))
      (is (instance? File (:body response)))
      (is (= (.getName (:body response)) "index.html"))))

  (testing "main not-found route"
    (let [response (app (request :get "/invalid"))]
      (is (= (:status response) 404))))

  (testing "not-found route"
    (let [response (app (request :get "/api/invalid"))]
      (is (= (:status response) 404))))
  )

(run-tests)