(ns cravings.views.welcome
  (:require [cravings.views.common :as common]
            [noir.content.getting-started]
            [monger.core :as mg])
  (:use [noir.core :only [defpage]]))



(defpage "/welcome" []
         (common/layout
           [:p "Welcome to cravings"]))
