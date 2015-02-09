(ns mtg-proxy-pdf.decklist-parser-test
  (:require [clojure.test :refer :all]
            [mtg-proxy-pdf.decklist-parser :refer :all]))

(def test-decklist-string "Academy Rector\nBirthing Pod\n3* Fall of the Hammer\n2 Kitchen Finks\n1 Lich's Mirror\n4 Mana Flair")
(def test-comma-separated-decklist-string "Academy Rector,Birthing Pod,3* Fall of the Hammer,2x Kitchen Finks,1 Lich's Mirror,4 Mana Flair")
(def test-dash-separated-decklist-string "Academy Rector---Birthing Pod---3* Fall of the Hammer---2   Kitchen Finks---1 Lich's Mirror---4 Mana Flair")

(def test-decklist (list (card-record "Academy Rector" 1)
                         (card-record "Birthing Pod" 1)
                         (card-record "Fall Of The Hammer" 3)
                         (card-record "Kitchen Finks" 2)
                         (card-record "Lich's Mirror" 1)
                         (card-record "Mana Flair" 4)))

(deftest get-card-id-test
  (testing "it generates a card id given a card name"
    (is (= (keyword "Academy Rector") (get-card-id "Academy Rector")))))

(deftest card-record-test
  (testing "it builds a card record with an id, name, and quantity"
    (is (= {:id (get-card-id "Academy Rector") :name "Academy Rector" :quantity 1} (card-record "Academy Rector" 1)))))

(deftest parse-card-name-no-quantity-test
  (testing "it parses a card name with no quantity"
    (is (= (card-record "Academy Rector" 1) (parse-card-name-quantity "Academy Rector")))))

(deftest parse-card-name-quantity-test
  (testing "it parses a card name with space-separated quantity"
    (is (= (card-record "Academy Rector" 2) (parse-card-name-quantity "2 Academy Rector")))))

(deftest parse-card-name-quantity-multi-space-test
  (testing "it parses a card name and quantity with multiple spaces"
    (is (= (card-record "Academy Rector" 3) (parse-card-name-quantity "3    Academy Rector")))))

(deftest parse-card-name-quantity-with-x-test
  (testing "it parses a card name with an optional 'x'"
    (is (= (card-record "Academy Rector" 4) (parse-card-name-quantity "4x Academy Rector")))))

(deftest parse-card-name-quantity-with-star-test
  (testing "it parses a card name with an optional '*'"
    (is (= (card-record "Academy Rector" 4) (parse-card-name-quantity "4* Academy Rector")))))

(deftest parse-card-name-capitalizes-name-test
  (testing "it parses a card name and capitalizes each word"
    (is (= (card-record "Academy Rector" 1) (parse-card-name-quantity "1 academy rector")))))

(deftest parse-decklist-string-test
  (testing "it converts a string into a decklist with a card name and quantity"
    (is (= test-decklist (parse-decklist-string test-decklist-string)))))

(deftest parse-decklist-string-alt-delim-test
  (testing "it converts a string with an alternative delimiter"
    (is (= test-decklist (parse-decklist-string test-comma-separated-decklist-string ",")))))

(deftest parse-decklist-string-mulit-delim-test
  (testing "it converts a string with a multi-character delimiter"
    (is (= test-decklist (parse-decklist-string test-dash-separated-decklist-string "---")))))

(deftest parse-text-file-test
  (testing "it parses a text file"
    (is (= test-decklist (parse-text-file "test/templates/decklist.txt")))))
