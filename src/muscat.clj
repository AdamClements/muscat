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
   & {:keys [coerce-payload-fn connection-lost failed-message]
      :or {coerce-payload-fn ednbytes->clj
           failed-coercion  default-failed-fn
           connection-lost  default-connection-lost-fn}}]
  (try
    (subscribe* uri topics callback coerce-payload-fn connection-lost failed-message)
    {:successful? true}
     (catch Exception cause
       {:successful? false
        :cause cause})))

(defn publish
  "Publish will publish to the given MQTT broker/topic the given
  data. By default the data will be coerced to a String of EDN data,
  however this can be overridden with the :coerce-payload-fn option.

  This will return true if the message has been delivered according to
  the qos contract and false if it has not (if for example it is not
  possible to connect)"
  [uri topic data
   & {:keys [coerce-payload-fn qos retained? timeout]
      :or {coerce-payload-fn clj->ednbytes
           qos              :at-least-once
           timeout          30000
           retained?        false}}]
  (let [qos-int ({:fire-and-forget 0 :at-least-once 1 :exactly-once 2} qos)]
   (try
     (publish* uri topic data qos-int coerce-payload-fn retained? timeout)
     {:successful? true}
     (catch Exception cause
       {:successful? false
        :cause cause}))))
