(ns mtg-proxy-pdf.core-test
  (:require [clojure.test :refer :all]
            [mtg-proxy-pdf.core :refer :all]
            [mtg-proxy-pdf.decklist-parser :as decklist-parser]
            [clojure.java.io :as io]))

(def test-query-url "http://magiccards.info/query?q=Academy%20Rector&v=card&s=cname")
(def test-card-name "Academy Rector")
(def test-image-url "http://magiccards.info/scans/en/ud/1.jpg")
(def test-image-url-list '("http://magiccards.info/scans/en/ud/1.jpg" "http://magiccards.info/scans/en/nph/104.jpg" "http://magiccards.info/scans/en/mma/190.jpg"))
(def test-card-record { :name "Academy Rector", :quantity 1 })
(def test-decklist [{ :name "Academy Rector", :quantity 1 }
                    { :name "Birthing Pod",   :quantity 1 }
                    { :name "Kitchen Finks",  :quantity 1 }])
(def test-decklist-images (decklist->images-urls test-decklist))
(def test-out-file-name "test")
(def test-out-file (io/as-file test-out-file-name))
(def test-in-file-name "test/templates/decklist.txt")
(def test-in-file (io/as-file "test/templates/decklist.txt"))

(deftest build-query-url-test
  (testing "it builds a url to magiccards.info"
    (is (= test-query-url (build-query-url test-card-record)))))

(deftest image-url-test
  (testing "it returns the url to the card image"
    (is (= test-image-url (image-url test-card-name test-query-url)))))

(deftest decklist->images-urls-test
  (testing "it converts a decklist to a list of image urls"
    (is (= test-image-url-list test-decklist-images))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; SIDE-EFFECTS TESTS
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(deftest decklist->images-urls-from-parsed-file-test
  (testing "it converts a decklist to a list of image urls from a parsed file"
    (is (= test-image-url-list (decklist->images-urls (decklist-parser/parse-text-file test-in-file-name))))))

(deftest images->html-test
  (testing "it writes the card images to html"
    (io/delete-file test-out-file-name true) ;; true to ignore error if file doesn't exist
    (images->html test-decklist-images test-out-file-name)
    (is (.exists test-out-file))))

(deftest images->pdf-test
  (testing "it writes the card images to a pdf"
    (io/delete-file test-out-file-name true) ;; true to ignore error if file doesn't exist
    (images->pdf test-decklist-images test-out-file-name)
    (is (.exists test-out-file))))

(deftest generate-test
  (testing "takes a decklist file and creates an html page with the images"
    (is (.exists test-in-file)) ;; ensure our input file exists
    (io/delete-file test-out-file-name true) ;; delete output file if it exists
    (generate test-in-file-name test-out-file-name)
    (is (.exists test-out-file))))