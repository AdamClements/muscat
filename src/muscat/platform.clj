(ns muscat.platform
  (:import (org.eclipse.paho.client.mqttv3 MqttCallback MqttClient)
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
    (letfn [(disconnect [] (doto client (.disconnect)) connect)
            (connect    [] (doto client (.connect) (.subscribe (into-array topics))) disconnect)]
      disconnect)))

(def connection-pool
  (memoize
   (fn [uri]
     (doto (MqttClient. uri (MqttClient/generateClientId) (MemoryPersistence.))
       (.setCallback (reify MqttCallback
                       (deliveryComplete [this token])
                       (connectionLost   [this cause]
                         (println "Lost connection on publisher pool " uri cause)) ;TODO fix up
                       (messageArrived   [this topic message])))
       (.connect)))))

(defn publish* [uri topic data qos payload-coercion retained?]
  (let [client (connection-pool uri)]
    (.publish client topic (payload-coercion data) qos retained?)))
