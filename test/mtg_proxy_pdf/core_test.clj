(ns mtg-proxy-pdf.core-test
  (:require [clojure.test :refer :all]
            [mtg-proxy-pdf.core :refer :all]
            [net.cgrand.enlive-html :as enlive]
            [ring.util.codec :as codec]
            [clj-pdf.core :as pdf]
            [hiccup.core :as hiccup]
            [hiccup.element :as element]))


(def test-template "test/templates/academy-rector.html")

(def query-url "http://magiccards.info/query?q=Academy+Rector&v=card&s=cname")

(def decklist [{ :name "Academy Rector", :quantity 1 }
               { :name "Birthing Pod",   :quantity 1 }
               { :name "Kitchen Finks",  :quantity 1 }])

(defn build-query-url [card-record]
  (let [{:keys [name]} card-record]
    (format "http://magiccards.info/query?q=%s&v=card&s=cname" (codec/url-encode name))))

(build-query-url (first decklist))

(defn image-url [card-name query-url]
  (-> query-url
      (java.net.URL.)
      (enlive/html-resource)
      (enlive/select [[:img (enlive/attr= :alt card-name)]])
      (first)
      (:attrs)
      (:src)))

(image-url (:name (first decklist)) (build-query-url (first decklist)))

(defn decklist->images-urls [decklist]
  (let [names (map :name decklist)
        urls (map build-query-url decklist)]
    (map image-url names urls)))

(decklist->images-urls decklist)

;; todo: include css to set the margin and padding
;; todo: use template

(defn images->html [images file-name]
  (spit file-name (hiccup/html (map (fn [image] (element/image { :width 222 :height 315} image)) images))))

(images->html (decklist->images-urls decklist) "test.html")

(defn images->pdf [images file-name]
  (pdf/pdf
   [{}
    (map (fn [image] [:image {:xscale 0.5
                              :yscale 0.5
                              :align  :center}
                      image]) images)]
   file-name))

(images->pdf (decklist->images-urls decklist) "test.pdf")
