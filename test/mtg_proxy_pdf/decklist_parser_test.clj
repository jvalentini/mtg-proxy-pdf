(ns mtg-proxy-pdf.decklist-parser-test
  (:require [clojure.test :refer :all]
            [mtg-proxy-pdf.decklist-parser :refer :all]))

(def test-decklist-string "Academy Rector\nBirthing Pod\nKitchen Finks")

(def test-decklist [{ :name "Academy Rector", :quantity 1 }
                    { :name "Birthing Pod",   :quantity 1 }
                    { :name "Kitchen Finks",  :quantity 1 }])

(deftest parse-card-name-quantity-test
  (testing "it parses a card name and quantity from a line"
    (is (= {:quantity 1 :name "Academy Rector"} (parse-card-name-quantity "Academy Rector")))))

(deftest parse-decklist-string-test
  (testing "it converts a string into a decklist with a card name and quantity"
    (is (= test-decklist (parse-decklist-string test-decklist-string)))))