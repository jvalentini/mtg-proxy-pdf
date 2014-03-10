(ns mtg-proxy-pdf.decklist-parser
  (:require [clojure.string :refer [split capitalize join]]))

(defn parse-quantity
  [quantity]
  (Integer. (or quantity 1)))

(defn parse-card-name
  [card-name]
  (join " " (map capitalize (split card-name #" "))))

(defn parse-record
  [record]
  (let [[_ quantity card-name] (re-find #"(^\d)?\s*[x|\*]?\s*[\'|\"]?(.*)[\'|\"]?" record)]
    [(parse-quantity quantity) (parse-card-name card-name)]))

(defn parse-card-name-quantity
  [record]
  (let [[quantity card-name] (parse-record record)]
    { :quantity quantity :name card-name }))

(defn parse-decklist-string
  ([decklist-string]
     (parse-decklist-string decklist-string "\n"))
  ([decklist-string delimiter]
     (map parse-card-name-quantity (split decklist-string (re-pattern delimiter)))))

(defn parse-text-file
  [file-name]
  (parse-decklist-string (slurp file-name)))
