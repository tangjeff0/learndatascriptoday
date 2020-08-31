(ns datascript-template.http
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [clojure.edn :as edn]
            [datascript.core :as d]))


;; https://www.notion.so/athensresearch/Datascript-Attribute-Table-afd0e5c9556b404880a0b149f87e9d6d

;; Each IDE plugin gives helpful commands to interact with the REPL
;; - Send Top Form to REPL
;; - Send Form Below Caret to REPL
;; - Switch REPL NS to Current File
(go (let [response (<! (http/get "https://api.github.com/users"
                                 {:with-credentials? false
                                  :query-params {"since" 135}}))]
      (prn (:status response)) ;; should get 200 in Browser DevTools Console
      (prn (map :login (:body response))))) ;;



(def athens-url "https://raw.githubusercontent.com/athensresearch/athens/master/data/athens.datoms")
(def roam-url "https://raw.githubusercontent.com/athensresearch/athens/master/data/help.datoms")

(defn to-edn-data
  "Deseralize data from HTTP response body into Clojure data structures."
  [response]
  (->> response
       :body
       js/JSON.parse
       js->clj
       (map edn/read-string)))

(defn to-datoms
  "Data is a flat vector but we need to chunk it into 3-tuples."
  [edn-data]
  (partition 3 edn-data))

(defn to-tx-data
  "Drop first tuple, which is [?e ?a ?v]. Add datascript :db/add \"facts\"."
  [datoms]
  (->> datoms
       rest
       (map #(cons :db/add %))))


(defonce response-atom (atom nil))

(go (let [response (<! (http/get athens-url {:with-credentials? false}))]
      (reset! response-atom response)))


@response-atom ;; Evaluate directly to return data as is. Might be a lot of data.
(prn @response-atom) ;; Wrap in prn to log values to console.
(-> @response-atom ;; Or use the thread first or last macros -> or ->>. These pipe data like Unix pipes.
    prn)

;; Taking 10 for all of these. Too much data otherwise

;; Data is a flat vector like https://raw.githubusercontent.com/athensresearch/athens/master/data/athens.datoms
(->> @response-atom
     to-edn-data
     (take 10))

;; Shape into 3-tuples
(->> @response-atom
     to-edn-data
     to-datoms
     (take 10))

;; Prepare for datascript transactions: https://docs.datomic.com/on-prem/transactions.html#list-forms
(->> @response-atom
     to-edn-data
     to-datoms
     to-tx-data
     (take 10))

(defn wrangle
  [response-atom]
  (->> @response-atom
       to-edn-data
       to-datoms
       to-tx-data))
