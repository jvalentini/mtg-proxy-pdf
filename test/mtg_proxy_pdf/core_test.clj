(ns mtg-proxy-pdf.core-test
  (:require [clojure.test :refer :all]
            [mtg-proxy-pdf.core :refer :all]
            [net.cgrand.enlive-html :as enlive]
            [ring.util.codec :as codec]
            [clj-pdf.core :as pdf]
            [hiccup.core :as hiccup]
            [hiccup.element :as element]))

;; todo: include css to set the margin and padding
;; todo: use template

(def test-template "test/templates/academy-rector.html")
(def test-query-url "http://magiccards.info/query?q=Academy%20Rector&v=card&s=cname")
(def test-card-name "Academy Rector")
(def test-image-url "http://magiccards.info/scans/en/ud/1.jpg")
(def test-image-url-list '("http://magiccards.info/scans/en/ud/1.jpg" "http://magiccards.info/scans/en/nph/104.jpg" "http://magiccards.info/scans/en/mma/190.jpg"))
(def test-card-record { :name "Academy Rector", :quantity 1 })
(def test-decklist [{ :name "Academy Rector", :quantity 1 }
                    { :name "Birthing Pod",   :quantity 1 }
                    { :name "Kitchen Finks",  :quantity 1 }])

(defn build-query-url [card-record]
  (let [{:keys [name]} card-record]
    (format "http://magiccards.info/query?q=%s&v=card&s=cname" (codec/url-encode name))))

(deftest build-query-url-test
  (testing "it builds a url to magiccards.info"
    (is (= test-query-url (build-query-url test-card-record)))))

(defn image-url [card-name query-url]
  (-> query-url
      (java.net.URL.)
      (enlive/html-resource)
      (enlive/select [[:img (enlive/attr= :alt card-name)]])
      (first)
      (:attrs)
      (:src)))

(image-url (:name (first test-decklist)) (build-query-url (first test-decklist)))

(deftest image-url-test
  (testing "it returns the url to the card image"
    (is (= test-image-url (image-url test-card-name test-query-url)))))

(defn decklist->images-urls [decklist]
  (let [names (map :name decklist)
        urls (map build-query-url decklist)]
    (map image-url names urls)))

(decklist->images-urls test-decklist)

(deftest decklist->images-urls-test
  (testing "it builds a url to magiccards.info"
    (is (= test-image-url-list (decklist->images-urls test-decklist)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; BEWARE: SIDE-EFFECTS LIVE BELOW!!!
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn images->html [images file-name]
  (spit file-name (hiccup/html (map (fn [image] (element/image { :width 222 :height 315} image)) images))))

(images->html (decklist->images-urls test-decklist) "test.html")

(defn images->pdf [images file-name]
  (pdf/pdf
   [{}
    (map (fn [image] [:image {:xscale 0.5
                              :yscale 0.5
                              :align  :center}
                      image]) images)]
   file-name))

(images->pdf (decklist->images-urls test-decklist) "test.pdf")
