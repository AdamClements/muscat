(ns muscat.coercions
  (:require [clojure.edn :as edn])
  (:import (java.io ByteArrayInputStream InputStreamReader
                    PushbackReader)))

(defn stringbytes->str [^bytes payload]
  (String. payload "UTF-8"))

(defn str->stringbytes [data]
  (.getBytes data "UTF-8"))

(defn ednbytes->clj [^bytes payload]
  (edn/read-string (String. payload "UTF-8")))

(defn clj->ednbytes [data]
  (str->stringbytes
   (binding [*print-length* nil]
     (pr-str data))))
