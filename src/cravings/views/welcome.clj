(ns cravings.views.welcome
  (:require [cravings.views.common :as common]
            [noir.content.getting-started]
            [monger.core :as mg]
            [monger.collection :as mc])
  (:use [noir.core :only [defpage]]))

(def mongo-uri "mongodb://127.0.0.1/")
(def config { :uri "mongodb://127.0.0.1/cravings", :db "cravings"})

(mg/connect-via-uri! (:uri config))

(defpage "/welcome" []
         (common/layout
           [:p "Welcome to cravings"]
           [:p (str "There are " (mc/count "cravings") " users.")]))
