(ns mongo
  (:require [monger.collection :as mc]
            [monger.core :as mg]
            [clojure.math.combinatorics :as combo]
            ))

(def ^:const tournamentCollectionName "tournaments")

(defn init-db [name]
  (mg/connect!)
  (mg/set-db! (mg/get-db name)))

(defn init-db-prod []
  (println "Connecting to Production Database")
  (init-db "versus"))

(defn createTournament [tournament]
  (if-not (contains? tournament :contenders)
    (assoc tournament :contenders []))
  (mc/insert tournamentCollectionName tournament))

(defn updateTournament [tournament]
  (mc/update-by-id tournamentCollectionName (tournament :_id) {"$set" (dissoc tournament :_id)}))

(defn getContenders [tournId]
  (get (mc/find-by-id tournamentCollectionName tournId [:contenders]) "contenders"))

(defn getTournament [_id]
  (mc/find-map-by-id tournamentCollectionName _id))

(defn generatePairs [tournament]
  (def comboSeq (combo/combinations (keys (getContenders tournament)) 2))
  (into #{} (for [pair comboSeq] (into #{} pair))))

(defn addContender [tournament contenderName]
  (mc/update-by-id tournamentCollectionName tournament {"$set" {(str "contenders." contenderName) 0}}))

(defn addSeveralContenders [tournament contenders]
  (doseq [contender contenders] (addContender tournament contender)))

(defn vote [tournament {:keys [winner loser]}]
  (mc/update-by-id tournamentCollectionName tournament {"$inc" {(str "contenders." winner) 1 (str "contenders." loser) -1}}))

(defn getAllTournaments [] (mc/find-maps tournamentCollectionName))

(defn deleteTournament [_id] (mc/remove-by-id tournamentCollectionName _id))
