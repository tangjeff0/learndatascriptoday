(ns datascript-template.core
  (:require [datascript.core :as d]
            [reagent.core :as r]
            [reagent.dom :as r-dom]))

(defonce app-state (r/atom {:text "Hello world!"}))

(defn hello-world []
  [:div#container
   [:div#left-sidebar
    "datascript"]
   [:div#center
     [:h1 "My Datascript App"]]])

(defn start []
  (r-dom/render [hello-world]
                (. js/document (getElementById "app"))))

(defn ^:export init []
  (start))

(defn stop []
  (js/console.log "stop"))
