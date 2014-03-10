(ns mtg-proxy-pdf.core-test
  (:require [clojure.test :refer :all]
            [mtg-proxy-pdf.core :refer :all]
            [mtg-proxy-pdf.decklist-parser :as decklist-parser]
            [clojure.java.io :as io]))

(def test-card-name "Academy Rector")
(def test-query-url "http://magiccards.info/query?q=Academy%20Rector&v=card&s=cname")
(def test-image-url "http://magiccards.info/scans/en/ud/1.jpg")

;; (def test-card-name "Archangel of Thune")
;; (def test-query-url "http://magiccards.info/query?q=Archangel%20of%20Thune&v=card&s=cname")
;; (def test-image-url "http://magiccards.info/scans/en/m14/5.jpg")

;; (def test-card-name "Avacyn's Pilgrim")
;; (def test-query-url "http://magiccards.info/query?q=Avacyn%27s%20Pilgrim&v=card&s=cname")
;; (def test-image-url "http://magiccards.info/scans/en/isd/170.jpg")

(def test-card-record { :name test-card-name, :quantity 1 })
(def test-decklist [test-card-record
                    { :name "Birthing Pod",   :quantity 1 }
                    { :name "Kitchen Finks",  :quantity 1 }])
(def test-image-url-list `(~test-image-url "http://magiccards.info/scans/en/nph/104.jpg" "http://magiccards.info/scans/en/mma/190.jpg"))
(def test-decklist-images (decklist->images-urls test-decklist))

(def test-query-urls '("http://magiccards.info/query?q=Academy%20Rector&v=card&s=cname" "http://magiccards.info/query?q=Angelic%20Renewal&v=card&s=cname" "http://magiccards.info/query?q=Archangel%20Of%20Thune&v=card&s=cname" "http://magiccards.info/query?q=Ashen%20Rider&v=card&s=cname" "http://magiccards.info/query?q=Avacyn%27s%20Pilgrim&v=card&s=cname" "http://magiccards.info/query?q=Barren%20Moor&v=card&s=cname" "http://magiccards.info/query?q=Bayou&v=card&s=cname" "http://magiccards.info/query?q=Birds%20Of%20Paradise&v=card&s=cname" "http://magiccards.info/query?q=Birthing%20Pod&v=card&s=cname"))
(def test-card-names '("Academy Rector" "Angelic Renewal" "Archangel Of Thune" "Ashen Rider" "Avacyn's Pilgrim" "Barren Moor" "Bayou" "Birds Of Paradise" "Birthing Pod"))
(def test-images '("http://magiccards.info/scans/en/ud/1.jpg" "http://magiccards.info/scans/en/wl/120.jpg" "http://magiccards.info/scans/en/m14/5.jpg" "http://magiccards.info/scans/en/ths/187.jpg" "http://magiccards.info/scans/en/isd/170.jpg" "http://magiccards.info/scans/en/c13/277.jpg" "http://magiccards.info/scans/en/rv/283.jpg" "http://magiccards.info/scans/en/m12/165.jpg" "http://magiccards.info/scans/en/nph/104.jpg"))

(def test-out-file-name "test")
(def test-out-file (io/as-file test-out-file-name))
(def test-in-file-name "test/templates/decklist.txt")
(def test-in-file (io/as-file test-in-file-name))

(deftest build-query-url-test
  (testing "it builds a url to magiccards.info"
    (is (= test-query-url (build-query-url test-card-record)))))

(deftest image-url-test
  (testing "it returns the url to the card image"
    (is (= test-image-url (image-url test-card-name test-query-url)))))

(deftest image-urls-test
  (testing "it returns the urls of multiple cards"
    (is (= test-images (map image-url test-card-names test-query-urls)))))

(deftest cache-uri-test
  (testing "it caches a retrieved uri onto disk"
    (let [expected-file (io/as-file "1.jpg")]
      (is (.exists expected-file))
      (is (= expected-file (cache-uri "http://magiccards.info/scans/en/ud/1.jpg"))))))

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