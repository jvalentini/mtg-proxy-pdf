(ns mtg-proxy-pdf.decklist-parser-test
  (:require [clojure.test :refer :all]
            [mtg-proxy-pdf.decklist-parser :refer :all]))

(def test-decklist-string "Academy Rector\nBirthing Pod\nKitchen Finks")

(def test-decklist [{ :name "Academy Rector", :quantity 1 }
                    { :name "Birthing Pod",   :quantity 1 }
                    { :name "Kitchen Finks",  :quantity 1 }])

(deftest parse-card-name-no-quantity-test
  (testing "it parses a card name with no quantity"
    (is (= {:quantity 1 :name "Academy Rector"} (parse-card-name-quantity "Academy Rector")))))

(deftest parse-card-name-quantity-test
  (testing "it parses a card name with space-separated quantity"
    (is (= {:quantity 2 :name "Academy Rector"} (parse-card-name-quantity "2 Academy Rector")))))

(deftest parse-card-name-quantity-multi-space-test
  (testing "it parses a card name and quantity with multiple spaces"
    (is (= {:quantity 3 :name "Academy Rector"} (parse-card-name-quantity "3    Academy Rector")))))

(deftest parse-decklist-string-test
  (testing "it converts a string into a decklist with a card name and quantity"
    (is (= test-decklist (parse-decklist-string test-decklist-string)))))