(ns cravings.views.welcome
  (:require [cravings.views.common :as common]
            [noir.content.getting-started]
            [monger.core :as mg]
            [monger.collection :as mc]
            [monger.query :as mq])
  (:use [noir.core :only [defpage]]
        monger.operators
        hiccup.element)
  (:import [org.bson.types BSONTimestamp]))

(def mongo-uri "mongodb://127.0.0.1/")
(def config {:uri "mongodb://127.0.0.1/cravings"
             :users "users"
             :strats "strats"
             :cravings "cravings"})

(mg/connect-via-uri! (:uri config))

(defn make-user
  "Add a user"
  [user-name e-mail first-name last-name age]
  (mc/insert (:users config)
             {:user user-name
              :mail e-mail
              :first first-name
              :last last-name
              :age age
              :points 0}))

(defn make-strat
  "Add a strategy to database"
  [strategy]
  (mc/insert (:strats config) { :content strategy }))

(defn get-random-strat []
  (let [strats (mc/find-maps "strats")
        index (rand-int (count strats))]
    (:content (nth strats index))))

(defn make-craving
  "Generate a craving object"
  [user-name]
  (mc/insert (:cravings config) {:timestamp (BSONTimestamp.)
                                 :user user-name
                                 :result 0}))

(defn get-random-record
  "Get a random record"
  [coll]
  (nth coll (rand-int (count coll))))

(defn update-score [user amount]
  (mc/update (:users config)
             {:user (:user user)}
             {"$inc" {:points amount}}))

(def user
  (first (mc/find-maps "users" {:user "tobrien"})))

(defpage "/" []
  (common/layout
   [:h1 "Cravings app"]
   [:p (get-random-strat)]
   [:p (str "Tom O'Brien - " (:points user) " points.")]
   [:p ]
   [:p
    (link-to "/test" "I gave in")
    "-"
    (link-to "/test" "I didn't give in!")]))

(defpage [:post "/login"] {:keys [username password]}
  (str "You tried to login as " username " with the password " password))

(defpage "/user/:first-name" {:keys [id]})

(defpage "/login" []
  (common/layout
   [:h1 "Log-in"]))

(defpage "/welcome" []
         (common/layout
           [:p "Welcome to cravings"]
           [:p (str "There are " (mc/count "users") " users.")]))
