(ns mtg-proxy-pdf.decklist-parser-test
  (:require [clojure.test :refer :all]
            [mtg-proxy-pdf.decklist-parser :refer :all]
            [clojure.java.io :as io]))

(def test-decklist-string "Academy Rector\nBirthing Pod\n2 Kitchen Finks\n3* Fall of the Hammer\n2 Lich's Mirror\n12 Mana Flair")
(def test-comma-separated-decklist-string "Academy Rector,Birthing Pod,2x Kitchen Finks,3* Fall of the Hammer,2 Lich's Mirror,12 Mana Flair")
(def test-dash-separated-decklist-string "Academy Rector---Birthing Pod---2   Kitchen Finks---3* Fall of the Hammer---2 Lich's Mirror---12 Mana Flair")
(def test-decklist [{ :name "Academy Rector",     :quantity 1 }
                    { :name "Birthing Pod",       :quantity 1 }
                    { :name "Kitchen Finks",      :quantity 2 }
                    { :name "Fall Of The Hammer", :quantity 3 }
                    { :name "Lich's Mirror",      :quantity 2 }
                    { :name "Mana Flair",         :quantity 12 }])

(deftest parse-card-name-no-quantity-test
  (testing "it parses a card name with no quantity"
    (is (= {:quantity 1 :name "Academy Rector"} (parse-card-name-quantity "Academy Rector")))))

(deftest parse-card-name-quantity-test
  (testing "it parses a card name with space-separated quantity"
    (is (= {:quantity 2 :name "Academy Rector"} (parse-card-name-quantity "2 Academy Rector")))))

(deftest parse-card-name-quantity-multi-space-test
  (testing "it parses a card name and quantity with multiple spaces"
    (is (= {:quantity 3 :name "Academy Rector"} (parse-card-name-quantity "3    Academy Rector")))))

(deftest parse-card-name-quantity-with-x-test
  (testing "it parses a card name with an optional 'x'"
    (is (= {:quantity 4 :name "Academy Rector"} (parse-card-name-quantity "4x Academy Rector")))))

(deftest parse-card-name-capitalizes-name-test
  (testing "it parses a card name and capitalizes each word"
    (is (= {:quantity 1 :name "Academy Rector"} (parse-card-name-quantity "1 academy rector")))))

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
