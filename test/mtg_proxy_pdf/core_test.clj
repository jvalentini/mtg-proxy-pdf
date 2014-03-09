(ns mtg-proxy-pdf.core-test
  (:require [clojure.test :refer :all]
            [mtg-proxy-pdf.core :refer :all]
            [clojure.java.io :as io]))

(def test-template "test/templates/academy-rector.html")
(def test-query-url "http://magiccards.info/query?q=Academy%20Rector&v=card&s=cname")
(def test-card-name "Academy Rector")
(def test-image-url "http://magiccards.info/scans/en/ud/1.jpg")
(def test-image-url-list '("http://magiccards.info/scans/en/ud/1.jpg" "http://magiccards.info/scans/en/nph/104.jpg" "http://magiccards.info/scans/en/mma/190.jpg"))
(def test-card-record { :name "Academy Rector", :quantity 1 })
(def test-decklist [{ :name "Academy Rector", :quantity 1 }
                    { :name "Birthing Pod",   :quantity 1 }
                    { :name "Kitchen Finks",  :quantity 1 }])
(def test-decklist-images (mtg-proxy-pdf.core/decklist->images-urls test-decklist))
(def test-html-file-name "test.html")
(def test-html-file (io/as-file test-html-file-name))
(def test-pdf-file-name "test.html")
(def test-pdf-file (io/as-file test-pdf-file-name))

(deftest build-query-url-test
  (testing "it builds a url to magiccards.info"
    (is (= test-query-url (mtg-proxy-pdf.core/build-query-url test-card-record)))))

(deftest image-url-test
  (testing "it returns the url to the card image"
    (is (= test-image-url (mtg-proxy-pdf.core/image-url test-card-name test-query-url)))))

(deftest decklist->images-urls-test
  (testing "it builds a url to magiccards.info"
    (is (= test-image-url-list test-decklist-images))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; SIDE-EFFECTS TESTS
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(deftest images->html-test
  (testing "it writes the card images to html"
    (io/delete-file test-html-file-name true) ;; true to ignore error if file doesn't exist
    (images->html test-decklist-images test-html-file-name)
    (is (.exists test-html-file))))

(deftest images->pdf-test
  (testing "it writes the card images to a pdf"
    (io/delete-file test-pdf-file-name true) ;; true to ignore error if file doesn't exist
    (images->pdf test-decklist-images test-pdf-file-name)
    (is (.exists test-pdf-file))))
