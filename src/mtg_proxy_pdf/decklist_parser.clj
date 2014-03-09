(ns mtg-proxy-pdf.decklist-parser
  (:require [clojure.string :refer [split capitalize join]]))

(defn parse-card-name-quantity
  [record]
  (let [[_ quantity card-name] (re-find #"(^\d)?x?\s*(.*)" record)
        quantity (Integer. (or quantity 1))
        card-name (join " " (map capitalize (split card-name #" ")))]
    { :quantity quantity :name card-name }))

;; TODO: should parse out a quantity
;; TODO: should accept delimiters other than newline
(defn parse-decklist-string
  [decklist-string]
  (map parse-card-name-quantity (split decklist-string #"\n")))