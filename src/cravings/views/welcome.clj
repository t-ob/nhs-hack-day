(ns cravings.views.welcome
  (:require [cravings.views.common :as common]
            [noir.response :as resp]
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
  [user result]
  (mc/insert (:cravings config) {:timestamp (BSONTimestamp.)
                                 :user (:user user)
                                 :result result}))

(defn get-cravings [n]
  "Get list of n most recent cravings"
  (let [query (mq/with-collection (:cravings config)
                (mq/find {})
                (mq/sort { :timestamp 1})
                (mq/limit n))]
    (map #(hash-map :username (:user %) :result (:result %)) query)))

(defn get-random-record
  "Get a random record"
  [coll]
  (nth coll (rand-int (count coll))))

(defn update-score [user amount]
  (mc/update (:users config)
             {:user (:user user)}
             {"$inc" {:points amount}}))

(defn get-score [user]
  (:points (first (mc/find-maps (:users config) {:user (:user user)}))))

(def user
  (first (mc/find-maps "users" {:user "tobrien"})))

(defpage "/" []
  (common/layout
   [:h1 "Cravings app"]
   [:p (get-random-strat)]
   [:p (str "Tom O'Brien : "
            (get-score user)
            " points.")]
   [:p
    (link-to "/failed" "I gave in")
    " "
    (link-to "/overcame" "I didn't give in!")]
   [:p "Most recent cravings:"]
   (common/craving-list (get-cravings 10))
   ))

(defpage [:post "/login"] {:keys [username password]}
  (str "You tried to login as " username " with the password " password))

(defpage "/failed" []
  (update-score user -10)
  (make-craving user false)
  (resp/redirect "/"))

(defpage "/overcame" []
  (update-score user 25)
  (make-craving user true)
  (resp/redirect "/"))

(defpage "/login" []
  (common/layout
   [:h1 "Log-in"]))

(defpage "/test" []
  (update-score user 100)
  (resp/redirect "/"))

(defpage "/welcome" []
         (common/layout
           [:p "Welcome to cravings"]
           [:p (str "There are " (mc/count "users") " users.")]))
