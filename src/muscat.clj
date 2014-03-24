(ns muscat
  (:require [muscat.platform :refer [subscribe* publish*]]
            [muscat.coercions :refer :all]))

(defn default-failed-fn [uri topic byte-payload cause]
  (println "Received bad message from " uri " on topic " topic " because " cause))

(defn default-connection-lost-fn [uri cause]
  (println "Disconnected from " uri " because " cause))

(defn subscribe
  "Subscribe to the given topics on an MQTT broker at uri, calling
  callback with any incoming messages.

  Optionally provide a :payload-coercion function which takes ^bytes
  payload which will then get passed to the callback as the message.

  Optionally provide a :connection-lost callback"
  [uri topics callback
   & {:keys [payload-coercion connection-lost failed-message]
      :or {payload-coercion ednbytes->clj
           failed-coercion  default-failed-fn
           connection-lost  default-connection-lost-fn}}]
  (subscribe* uri topics callback payload-coercion connection-lost failed-message))

(defn publish
  "Publish will publish to the given MQTT broker/topic the given
  data. By default the data will be coerced to a String of EDN data,
  however this can be overridden with the :payload-coercion option"
  [uri topic data
   & {:keys [payload-coercion qos]}]
 (let [default-opts {:payload-coercion muscat/edn->bytes}])
  )