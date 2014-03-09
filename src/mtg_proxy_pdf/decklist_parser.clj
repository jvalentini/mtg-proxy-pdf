(ns mtg-proxy-pdf.decklist-parser
  (:require [clojure.string :refer [split capitalize join]]))

(defn parse-record
  [record]
  (let [[_ quantity card-name] (re-find #"(^\d)?x?\s*(.*)" record)]
    [(Integer. (or quantity 1)) (join " " (map capitalize (split card-name #" ")))]))

(defn parse-card-name-quantity
  [record]
  (let [[quantity card-name] (parse-record record)]
    { :quantity quantity :name card-name }))

;; TODO: should parse out a quantity
;; TODO: should accept delimiters other than newline
(defn parse-decklist-string
  [decklist-string]
  (map parse-card-name-quantity (split decklist-string #"\n")))