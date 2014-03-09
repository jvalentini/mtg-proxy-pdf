(ns mtg-proxy-pdf.core
  (:require [net.cgrand.enlive-html :as enlive]
            [ring.util.codec :as codec]
            [clj-pdf.core :as pdf]
            [hiccup.core :as hiccup]
            [hiccup.element :as element]))

;; todo: include css to set the margin and padding
;; todo: use template

;; URL where we can find the card images.
;; Expects a format specifier.
(def image-source-url "http://magiccards.info/query?q=%s&v=card&s=cname")

(defn build-query-url [card-record]
  (let [{:keys [name]} card-record]
    (format image-source-url (codec/url-encode name))))

(defn image-url [card-name query-url]
  (-> query-url
      (java.net.URL.)
      (enlive/html-resource)
      (enlive/select [[:img (enlive/attr= :alt card-name)]])
      (first)
      (:attrs)
      (:src)))

(defn decklist->images-urls [decklist]
  (let [names (map :name decklist)
        urls (map build-query-url decklist)]
    (map image-url names urls)))
