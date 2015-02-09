(defproject mtg-proxy-pdf "0.1.0-SNAPSHOT"
  :description "Given a list of magic cards, create a file (html or pdf) of the card images."
  :url "https://github.com/jvalentini/mtg-proxy-pdf"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [enlive "1.1.5"]
                 [clj-pdf "1.11.15"]
                 [ring/ring-codec "1.0.0"]
                 [hiccup "1.0.5"]
                 [org.clojure/data.json "0.2.4"]
                 [org.clojure/tools.cli "0.3.1"]]

  :profiles { :dev {:dependencies [[org.clojure/tools.trace "0.7.6"]
                                   [midje "1.5.1"]]}}
  :main mtg-proxy-pdf.core
  :aot [mtg-proxy-pdf.core])
