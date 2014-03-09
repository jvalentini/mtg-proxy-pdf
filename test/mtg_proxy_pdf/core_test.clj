(ns mtg-proxy-pdf.core-test
  (:require [clojure.test :refer :all]
            [mtg-proxy-pdf.core :refer :all]))

(def test-template "test/templates/academy-rector.html")
(def test-query-url "http://magiccards.info/query?q=Academy%20Rector&v=card&s=cname")
(def test-card-name "Academy Rector")
(def test-image-url "http://magiccards.info/scans/en/ud/1.jpg")
(def test-image-url-list '("http://magiccards.info/scans/en/ud/1.jpg" "http://magiccards.info/scans/en/nph/104.jpg" "http://magiccards.info/scans/en/mma/190.jpg"))
(def test-card-record { :name "Academy Rector", :quantity 1 })
(def test-decklist [{ :name "Academy Rector", :quantity 1 }
                    { :name "Birthing Pod",   :quantity 1 }
                    { :name "Kitchen Finks",  :quantity 1 }])

(deftest build-query-url-test
  (testing "it builds a url to magiccards.info"
    (is (= test-query-url (mtg-proxy-pdf.core/build-query-url test-card-record)))))

(deftest image-url-test
  (testing "it returns the url to the card image"
    (is (= test-image-url (mtg-proxy-pdf.core/image-url test-card-name test-query-url)))))

(deftest decklist->images-urls-test
  (testing "it builds a url to magiccards.info"
    (is (= test-image-url-list (mtg-proxy-pdf.core/decklist->images-urls test-decklist)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; BEWARE: SIDE-EFFECTS LIVE BELOW!!!
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn images->html [images file-name]
  (spit file-name (hiccup/html (map (fn [image] (element/image { :width 222 :height 315} image)) images))))

(images->html (mtg-proxy-pdf.core/decklist->images-urls test-decklist) "test.html")

(defn images->pdf [images file-name]
  (pdf/pdf
   [{}
    (map (fn [image] [:image {:xscale 0.5
                              :yscale 0.5
                              :align  :center}
                      image]) images)]
   file-name))

(images->pdf (mtg-proxy-pdf.core/decklist->images-urls test-decklist) "test.pdf")
