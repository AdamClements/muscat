(defproject muscat "0.1.0-SNAPSHOT"
  :description "Simple MQTT client for clojure and clojurescript"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]                 
                 [org.eclipse.paho/mqtt-client "0.4.0"]]
  
  :repositories {"eclipse-paho" {:url "https://repo.eclipse.org/content/groups/paho/"
                                 :snapshots false
                                 :releases {:checksum :fail}}})
