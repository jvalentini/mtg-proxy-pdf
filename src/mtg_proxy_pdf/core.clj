(ns mtg-proxy-pdf.core
  (:require [mtg-proxy-pdf.decklist-parser :as decklist-parser]
            [net.cgrand.enlive-html :as enlive]
            [ring.util.codec :as codec]
            [clj-pdf.core :as pdf]
            [hiccup.core :as hiccup]
            [hiccup.element :as element]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.data.json :as json])
  (:use [clojure.tools.trace]))

;; URL where we can find the card images.
;; Expects a format specifier.
(def image-source-url "http://magiccards.info/query?q=%s&v=card&s=cname")

;; Location of the image src cache for a card.
(def image-cache "image_sources.json")

(defn build-query-url [card-record]
  (let [{:keys [name]} card-record]
    (format image-source-url (codec/url-encode name))))

(defn cache-uri
  [uri]
  (let [out-file-name (.getName (io/file uri))]
    (with-open [in (io/input-stream uri)
                out (io/output-stream out-file-name)]
      (io/copy in out))
    (io/file out-file-name)))

(defn read-cache [file-name]
  (json/read-str (slurp file-name) :eof-error? false))

(defn fetch-image-src [card-record]
  (-> card-record
      (build-query-url)
      (java.net.URL.)
      (enlive/html-resource)
      (enlive/select [[:img (enlive/attr-contains :src "scans")]])
      (first)
      (:attrs)
      (:src)))

(defn get-cache [card-record]
  (let [key (str/lower-case (:name card-record))]
    (if (.exists (io/as-file image-cache))
      (get (read-cache image-cache) key)
      (spit image-cache ""))))

(defn write-to-cache [key value]
  (let [cached-image-sources (merge (read-cache image-cache) {key value})]
    (spit image-cache (json/write-str cached-image-sources))))

(defn cached-image-src [card-record]
  (let [key (str/lower-case (:name card-record))
        image-src (or (get-cache card-record)
                      (fetch-image-src card-record))]
    (write-to-cache key image-src)
    image-src))

(defn decklist->images-urls [decklist]
  (map cached-image-src decklist))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; BEWARE: SIDE-EFFECTS LIVE BELOW!!!
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn image-element
  [image]
  (element/image { :width 222 :height 315} image))

(defn images->html [images file-name]
  (spit file-name (hiccup/html (map image-element images))))

(defn images->pdf [images file-name]
  (pdf/pdf
   [{}
    (map (fn [image] [:image {:xscale 0.5
                              :yscale 0.5
                              :align  :center}
                      image]) images)]
   file-name))

(defn generate
  [in-file-name out-file-name]
  (images->html (decklist->images-urls (decklist-parser/parse-text-file in-file-name)) out-file-name))
