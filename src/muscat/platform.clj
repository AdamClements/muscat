(ns muscat.platform
  (:import (org.eclipse.paho.client.mqttv3 MqttCallback MqttClient)
           (org.eclipse.paho.client.mqttv3.persist MemoryPersistence)))

(def generate-id
  "Function to generate a unique ID based on the time and an atomic
   counter - should probably just be a rand-int"
  (let [counter (atom 0)]
    (fn [] (str "muscat-" (System/currentTimeMillis) "-" (swap! counter inc)))))

(defn subscribe* [uri topics callback payload-coercion connection-lost failed-message]
  (let [client (doto (MqttClient. uri (generate-id) (MemoryPersistence.))
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
