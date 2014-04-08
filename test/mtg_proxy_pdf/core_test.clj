(ns mtg-proxy-pdf.core-test
  (:require [clojure.test :refer :all]
            [mtg-proxy-pdf.core :refer :all]
            [mtg-proxy-pdf.decklist-parser :as decklist-parser]
            [mtg-proxy-pdf.decklist-parser :refer [get-card-id card-record]]
            [clojure.java.io :as io])
  (:use midje.sweet))

(def test-card-name "Academy Rector")
(def test-query-url "http://magiccards.info/query?q=Academy%20Rector&v=card&s=cname")
(def test-image-src "http://magiccards.info/scans/en/ud/1.jpg")

;; (def test-card-name "Archangel of Thune")
;; (def test-query-url "http://magiccards.info/query?q=Archangel%20of%20Thune&v=card&s=cname")
;; (def test-image-src "http://magiccards.info/scans/en/m14/5.jpg")

;; (def test-card-name "Avacyn's Pilgrim")
;; (def test-query-url "http://magiccards.info/query?q=Avacyn%27s%20Pilgrim&v=card&s=cname")
;; (def test-image-src "http://magiccards.info/scans/en/isd/170.jpg")

(def test-card-record (card-record test-card-name 1))
(def test-decklist (list test-card-record
                         (card-record "Birthing Pod"        1 )
                         (card-record "Fall Of The Hammer"  3 )
                         (card-record "Kitchen Finks"       2 )
                         (card-record "Lich's Mirror"       1 )
                         (card-record "Mana Flair"          4 )))

(def test-image-src-list `(~test-image-src
                           "http://magiccards.info/scans/en/nph/104.jpg"
                           "http://magiccards.info/scans/en/bng/93.jpg"
                           "http://magiccards.info/scans/en/bng/93.jpg"
                           "http://magiccards.info/scans/en/bng/93.jpg"
                           "http://magiccards.info/scans/en/mma/190.jpg"
                           "http://magiccards.info/scans/en/mma/190.jpg"
                           "http://magiccards.info/scans/en/ala/210.jpg"
                           "http://magiccards.info/scans/en/uh/81.jpg"
                           "http://magiccards.info/scans/en/uh/81.jpg"
                           "http://magiccards.info/scans/en/uh/81.jpg"
                           "http://magiccards.info/scans/en/uh/81.jpg"))

(def test-decklist-images (decklist->images-urls test-decklist))

(def test-large-decklist (list (card-record "Academy Rector" 1)
                               (card-record "Angelic Renewal" 1)
                               (card-record "Archangel Of Thune" 1)
                               (card-record "Ashen Rider" 1)
                               (card-record "Avacyn's Pilgrim" 1)
                               (card-record "Barren Moor" 1)
                               (card-record "Bayou" 1)
                               (card-record "Birds of Paradise" 1)
                               (card-record "Birthing Pod" 1 )))

(def test-query-urls '("http://magiccards.info/query?q=Academy%20Rector&v=card&s=cname" "http://magiccards.info/query?q=Angelic%20Renewal&v=card&s=cname" "http://magiccards.info/query?q=Archangel%20Of%20Thune&v=card&s=cname" "http://magiccards.info/query?q=Ashen%20Rider&v=card&s=cname" "http://magiccards.info/query?q=Avacyn%27s%20Pilgrim&v=card&s=cname" "http://magiccards.info/query?q=Barren%20Moor&v=card&s=cname" "http://magiccards.info/query?q=Bayou&v=card&s=cname" "http://magiccards.info/query?q=Birds%20Of%20Paradise&v=card&s=cname" "http://magiccards.info/query?q=Birthing%20Pod&v=card&s=cname"))
(def test-card-names '("Academy Rector" "Angelic Renewal" "Archangel Of Thune" "Ashen Rider" "Avacyn's Pilgrim" "Barren Moor" "Bayou" "Birds Of Paradise" "Birthing Pod"))
(def test-images '("http://magiccards.info/scans/en/ud/1.jpg" "http://magiccards.info/scans/en/wl/120.jpg" "http://magiccards.info/scans/en/m14/5.jpg" "http://magiccards.info/scans/en/ths/187.jpg" "http://magiccards.info/scans/en/isd/170.jpg" "http://magiccards.info/scans/en/c13/277.jpg" "http://magiccards.info/scans/en/rv/283.jpg" "http://magiccards.info/scans/en/m12/165.jpg" "http://magiccards.info/scans/en/nph/104.jpg"))

(def test-out-file-name "test/target/test")
(def test-out-file (io/as-file test-out-file-name))
(def test-in-file-name "test/templates/decklist.txt")
(def test-in-file (io/as-file test-in-file-name))

(def test-out-file-name-pdf (apply str test-out-file-name ".pdf"))
(def test-out-file-pdf (io/as-file test-out-file-name-pdf))

(def test-out-file-name-html (apply str test-out-file-name ".html"))
(def test-out-file-html (io/as-file test-out-file-name-html))

(facts "about url building"
  (fact "build-query-url builds a valid url to magiccards.info"
    (build-query-url test-card-record)
    => test-query-url)
  (fact "fetch-image-src returns the url to the card image"
    (fetch-image-src test-card-record)
    => test-image-src)
  (fact "should return the urls of multiple cards"
    (map fetch-image-src test-large-decklist)
    => test-images))

;; (deftest cache-uri-test
;;   (testing "it caches a retrieved uri onto disk"
;;     (let [expected-file (io/as-file "test/templates/1.jpg")]
;;       (is (.exists expected-file))
;;       (is (= expected-file (cache-uri "http://magiccards.info/scans/en/ud/1.jpg"))))))

(facts "about desklists"
  ;; (fact "converts a decklist to a list of image urls"
  ;;   (test-decklist-images)
  ;;   => test-image-src-list)
  (fact "returns a card image src once for each quantity"
    (cached-image-src { :name "Kitchen Finks", :quantity 2})
    => '("http://magiccards.info/scans/en/mma/190.jpg" "http://magiccards.info/scans/en/mma/190.jpg"))
  (fact "creates a minimal decklist combines any cards in the list more than once"
    (reduce-decklist [(card-record "Kitchen Finks"  2 )
                      (card-record "Birthing Pod"   2 )
                      (card-record "Kitchen Finks"  1 )])
    =>  {(get-card-id "Kitchen Finks") (card-record "Kitchen Finks"  3 )
         (get-card-id "Birthing Pod")  (card-record "Birthing Pod"   2 )}))


(facts "about caching"
  (fact "it caches list of image sources"
    (cached-image-src test-card-record)
    => (list test-image-src))
  (fact "it can read from the cache"
    (get-cache test-card-record)
    => test-image-src))

(facts "about image and document generation"
  (fact "it converts a decklist to a list of image urls from a parsed file"
    (decklist->images-urls (decklist-parser/parse-text-file test-in-file-name))
    => test-image-src-list)
  (fact "it writes the card images to html"
    (io/delete-file test-out-file-name-html true) ;; true to ignore error if file doesn't exist
    (images->html test-decklist-images test-out-file-name-html)
    (.exists test-out-file-html)
    => truthy)
  (fact "it writes the card images to a pdf"
    (io/delete-file test-out-file-name-pdf true)
    (images->pdf test-decklist-images test-out-file-name-pdf)
    (.exists test-out-file-pdf)
    => truthy)
  (fact "takes a decklist file and creates an html page with the images"
    (.exists test-in-file) => truthy
    (io/delete-file test-out-file-name true)
    (generate test-in-file-name test-out-file-name)
    (.exists test-out-file) => truthy))

;; Couldn't get this test working right off the bat will take a look in the morning
;; In the mean time this test will still work even though it isn't in midje format
(deftest decklist->images-urls-test
  (testing "it converts a decklist to a list of image urls"
    (is (= test-image-src-list
           test-decklist-images))))
