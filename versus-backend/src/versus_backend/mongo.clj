(ns versus-backend.mongo
  (:require [monger.collection :as mc]
            [monger.core :as mg]
            [clojure.math.combinatorics :as combo]
            ))

(def ^:const tournamentCollectionName "tournaments")

(defn init-db [name]
  (mg/connect!)
  (mg/set-db! (mg/get-db name)))

(defn init-db-prod []
  (init-db "prod"))

(defn createTournament [shortName & [description]]
  (mc/insert tournamentCollectionName {:_id shortName :description description}))

(defn getContenders [tournament]
  (get (mc/find-by-id tournamentCollectionName tournament [:contenders]) "contenders"))

(defn generatePairs [tournament]
  (def comboSeq (combo/combinations (keys (getContenders tournament)) 2))
  (into #{} (for [pair comboSeq] (into #{} pair))))

(defn addContender [tournament contenderName]
  (mc/update-by-id tournamentCollectionName tournament {"$set" {(str "contenders." contenderName) 0}}))

(defn addSeveralContenders [tournament contenders]
  (doseq [contender contenders] (addContender tournament contender)))

(defn vote [tournament & {:keys [winner loser]}]
  (mc/update-by-id tournamentCollectionName tournament {"$inc" {(str "contenders." winner) 1 (str "contenders." loser) -1}}))

(defn getTournaments []
  (into #{} (map #(get % "_id") (mc/find tournamentCollectionName {} [:_id]))))
