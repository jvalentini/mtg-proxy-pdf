(ns mtg-proxy-pdf.core
  (:require [mtg-proxy-pdf.decklist-parser :as decklist-parser]
            [net.cgrand.enlive-html :as enlive]
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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; BEWARE: SIDE-EFFECTS LIVE BELOW!!!
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn images->html [images file-name]
  (spit (apply str file-name ".html") (hiccup/html (map (fn [image] (element/image { :width 222 :height 315} image)) images))))

(defn images->pdf [images file-name]
  (pdf/pdf
   [{}
    (map (fn [image] [:image {:xscale 0.5
                              :yscale 0.5
                              :align  :center}
                      image]) images)]
   (apply str file-name ".pdf")))

(defn generate
  [in-file-name out-file-name]
  (images->html (decklist->images-urls (decklist-parser/parse-text-file in-file-name)) out-file-name))