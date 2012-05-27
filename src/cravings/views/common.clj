(ns cravings.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page :only [include-css html5]]))

(defpartial list-item [{:keys [username result]}]
  (let [result-string (if (true? result) " overcame" " succumbed to")]
    [:li
     (str username result-string " a craving!")]))

(defpartial craving-list [items]
  [:ul#cravings
   (map list-item items)])

(defpartial layout [& content]
            (html5
              [:head
               [:title "cravings"]
               (include-css "/css/reset.css")]
              [:body
               [:div#wrapper
                content]]))

