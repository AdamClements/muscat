(ns muscat.platform
  (:import (org.eclipse.paho.client.mqttv3 MqttCallback MqttClient
                                           MqttConnectOptions)
           (org.eclipse.paho.client.mqttv3.persist MemoryPersistence)))

(defn subscribe* [uri topics callback payload-coercion connection-lost failed-message]
  (let [client (doto (MqttClient. uri (MqttClient/generateClientId) (MemoryPersistence.))
                 (.setCallback (reify MqttCallback
                                 (deliveryComplete [this token])
                                 (connectionLost   [this cause]
                                   (connection-lost uri cause))
                                 (messageArrived   [this topic message]
                                   (try
                                    (callback topic (payload-coercion (.getPayload message)))
                                    (catch Exception cause (failed-message uri topic message cause))))))
                 (.connect)
                 (.subscribe (into-array topics)))]
    (fn [] (when (.isConnected client) (doto client (.disconnect))))))

(defn fresh-connection [uri]
  (MqttClient. uri (MqttClient/generateClientId) (MemoryPersistence.)))

;;; Perhaps this should reconnect existing connections, rather than
;;; replacing them.
(def get-or-create-connection 
  (let [pool (atom {})]
    (fn [uri]
      (let [existing (@pool uri)]
       (if existing
         existing
         (get (swap! pool assoc uri (fresh-connection uri)) uri))))))

(defn get-connected-client [uri timeout]
  (let [client (get-or-create-connection uri)]
    (when-not (.isConnected client)
      (.connect client (doto (MqttConnectOptions.)
                         (.setConnectionTimeout timeout))))
    client))

(defn publish* [uri topic data qos payload-coercion retained? timeout]
  (let [client (get-connected-client uri timeout)]
    (.publish client topic (payload-coercion data) qos retained?)))
