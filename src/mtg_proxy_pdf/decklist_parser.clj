(ns mtg-proxy-pdf.decklist-parser
  (:require [clojure.string :refer [split]]))

(defn parse-card-name-quantity
  [record]
  (let [quantity 1
        card-name record]
    { :quantity quantity :name card-name }))

;; TODO: should parse out a quantity
;; TODO: should accept delimiters other than newline
(defn parse-decklist-string
  [decklist-string]
  (map parse-card-name-quantity (split decklist-string #"\n")))