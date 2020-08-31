(ns datascript-template.db
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [datascript-template.http :refer [response-atom wrangle]]
            [datascript.core :as d]))


;; Instantiate a database connection.
(defonce db (d/create-conn))

;; Transact the transaction data.
(d/transact! db (wrangle @response-atom))

;; You can now query the database!
;; This query returns all datoms.
(d/q '[:find ?e ?a ?v
       :in $
       :where [?e ?a ?v]]
  @db)
