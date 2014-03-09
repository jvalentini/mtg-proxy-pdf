(ns mtg-proxy-pdf.decklist-parser
  (:require [clojure.string :refer [split]]))

(defn parse-card-name-quantity
  [record]
  (let [parsed-record (re-find #"(^\d)?x?\s*(.*)" record)
        quantity (Integer. (or (second parsed-record) 1))
        card-name (nth parsed-record 2)]
    { :quantity quantity :name card-name }))

;; TODO: should parse out a quantity
;; TODO: should accept delimiters other than newline
(defn parse-decklist-string
  [decklist-string]
  (map parse-card-name-quantity (split decklist-string #"\n")))