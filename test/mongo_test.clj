(ns mongo-test
  (:require [clojure.test :refer :all]
            [mongo :refer :all]
            [monger.collection :as mc]
            [monger.core :as mg]
            [monger.db :as mdb]))

(defn db-setup-fixture [f]
  (mg/connect!)
  (def testDbName "test")
  (mdb/drop-db (mg/get-db testDbName))
  (println "creating test db")
  (init-db testDbName)
  (f))

(def testTournamentName "myTournie")

(defn re-create-tournament-fixture [f]
  (mc/drop tournamentCollectionName)
  (createTournament {:_id testTournamentName})
  (f))

(use-fixtures :once db-setup-fixture)

(use-fixtures :each re-create-tournament-fixture)


(deftest createNewCollection
  (testing "createTournament makes a new collection"
    ;given
    (mc/drop tournamentCollectionName)
    (is (not (mc/exists? tournamentCollectionName)))

    ;when
    (createTournament {:_id testTournamentName})

    ;then
    (is (mc/exists? tournamentCollectionName))))

(deftest tournamentNameUsedAsId
  (testing "tournament name is used as the collection id"
    ;given
    (mc/drop tournamentCollectionName)
    (is (not (mc/exists? tournamentCollectionName)))

    ;when
    (createTournament {:_id testTournamentName})

    ;then
    (is (= (get (mc/find-one-as-map tournamentCollectionName {:_id testTournamentName}) :_id) testTournamentName))))

(deftest descriptionArgTest
  (testing "createTournament accepts description argument and it gets persisted"
    ;given
    (mc/drop tournamentCollectionName)
    (is (not (mc/exists? tournamentCollectionName)))
    (def testTournamentDescription "awesome")

    ;when
    (createTournament {:_id testTournamentName :description testTournamentDescription})

    ;then
    (is (= (get (mc/find-one-as-map tournamentCollectionName {:_id testTournamentName}) :description) testTournamentDescription))))

(deftest contenderAutoVivify
  (testing "the contender list is automatically created for new tournaments"
    (createTournament {:_id "noContenders"})

    (def foundTourn (mc/find-map-by-id tournamentCollectionName "noContenders"))
    (is (= (foundTourn :contenders) {}))
    ))

(deftest addContenderTest
  (testing "adding a contender to a tournament"
    ;given
    (def contender "thing one")

    ;when
    (addContender testTournamentName contender)

    ;then
    (is (= (getContenders testTournamentName) {contender 0})))
  (testing "add a second contender to the tournament"
    ;given
    (def anotherContender "thing two")

    ;when
    (addContender testTournamentName anotherContender)

    ;then
    (is (= (getContenders testTournamentName) {contender 0 anotherContender 0}))))

(deftest addSeveralContendersTest
  (testing "several contenders can be added at once via a list"
    ;given
    (def contenders ["1" "2" "3"])

    ;when
    (addSeveralContenders testTournamentName contenders)

    ;then
    (is (= (getContenders testTournamentName) (zipmap contenders (repeat 0))))))

(deftest generatePairsTest
  (testing "paired combinations are properly generated from the contender list"
    ;given
    (addSeveralContenders testTournamentName [1 2 3 4])

    ;when
    (def pairs (generatePairs testTournamentName))

    ;then
    (is (= pairs #{#{"1" "2"} #{"1" "3"} #{"1" "4"} #{"2" "3"} #{"2" "4"} #{"3" "4"}}))))

(deftest writeAndReadPairVotes
  (testing "pairs and votes are persisted"
    ;given
    (addSeveralContenders testTournamentName ["first" "second" "third"])

    ;when
    (vote testTournamentName {:winner "first" :loser "second"})

    ;then
    (is (= (get (getContenders testTournamentName) "first") 1))
    (is (= (get (getContenders testTournamentName) "second") -1))))

(deftest lotsOfVotes
  (testing "lots of votes can happen"
    ;given
    (def contenders ["rice krispies" "lucky charms" "cap'n crunch" "frosted flakes"])
    (addSeveralContenders testTournamentName contenders)

    ;when
    (vote testTournamentName {:winner "rice krispies" :loser "lucky charms"})
    (vote testTournamentName {:winner "rice krispies" :loser "frosted flakes"})
    (vote testTournamentName {:winner "frosted flakes" :loser "cap'n crunch"})
    (vote testTournamentName {:winner "frosted flakes" :loser "lucky charms"})

    ;then
    (is (= (get (getContenders testTournamentName) "frosted flakes") 1))
    (is (= (get (getContenders testTournamentName) "rice krispies") 2))
    (is (= (get (getContenders testTournamentName) "cap'n crunch") -1))
    (is (= (get (getContenders testTournamentName) "lucky charms") -2))

    ))

(deftest findTournaments
  (testing "all tournament names are returned"
    ;given
    (createTournament {:_id "cereal"})
    (createTournament {:_id "people"})
    (createTournament {:_id "animals"})

    ;when
    (def tournaments (getAllTournaments))

    ;then
    (def expected #{testTournamentName "cereal" "people" "animals"})
    (def actual (into #{} (map (fn [x] (x :_id)) tournaments)))

    (is (= expected actual))
    ))

(deftest getSingleTournament
  (testing "retrieval of just one tournament with a few contenders"
    ;given
    (createTournament {:_id "tournamentX"})
    (addContender "tournamentX" "contender1")
    (addContender "tournamentX" "contender2")

    ;when
    (def actual (getTournament "tournamentX"))

    ;then
    (is (= {:_id "tournamentX" :contenders {:contender1 0 :contender2 0}} actual))))

(deftest removeTournament
  (testing "tournaments can be deleted"
    ;given
    (is (mc/any? tournamentCollectionName {:_id testTournamentName}))

    ;when
    (deleteTournament testTournamentName)

    ;then
    (is (not (mc/any? tournamentCollectionName {:_id testTournamentName})))
    )
  )

(deftest modifyATournament
  (testing "tournament properties can be updated"
    ;given
    (def myTournamentUpdate {:_id testTournamentName :description "a better description"})

    ;when
    (updateTournament myTournamentUpdate)

    ;then
    (def foundTournament (mc/find-map-by-id mongo/tournamentCollectionName testTournamentName))
    (is (= (foundTournament :_id) (myTournamentUpdate :_id)))
    (is (= (foundTournament :description) (myTournamentUpdate :description))))
  )

(run-tests)