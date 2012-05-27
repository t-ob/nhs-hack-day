(ns cravings.views.welcome
  (:require [cravings.views.common :as common]
            [noir.content.getting-started]
            [monger.core :as mg]
            [monger.collection :as mc]
            [monger.query :as mq])
  (:use [noir.core :only [defpage]]))

(def mongo-uri "mongodb://127.0.0.1/")
(def config { :uri "mongodb://127.0.0.1/cravings",
             :db "cravings",
             :strats "strats"})

(mg/connect-via-uri! (:uri config))

(defn make-user
  "Add a user"
  [user-name first-name last-name age]
  (mc/insert "cravings"
             {:user user-name
              :first first-name
              :last last-name
              :age age
              :points 0}))

(defn make-strat
  "Add a strategy to database"
  [strategy]
  (mc/insert "strats" { :content strategy }))

(defn get-random-record
  "Get a random record"
  [coll]
  (nth coll (rand-int (count coll))))



(defpage "/" []
  "helloooo")

(defpage [:post "/login"] {:keys [username password]}
  (str "You tried to login as " username " with the password " password))

(defpage "/user/:first-name" {:keys [id]})

(defpage "/login" []
  (common/layout
   [:h1 "Log-in"]))

(defpage "/welcome" []
         (common/layout
           [:p "Welcome to cravings"]
           [:p (str "There are " (mc/count "cravings") " users.")]))
