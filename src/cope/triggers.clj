(ns cope.triggers
  (:require [clojure.core.async :refer [chan go-loop <! >!]]
            [cope.sources :refer [versions]]))

(defn- set-ready [version]
  "set a version to ready"
  (assoc version :ready true))

(defprotocol Trigger
  "A stream of source versions ready to be built"
  (ready [this] "chan of versions ready to be built"))

;; Always triggers every version as soon as it sees it
(defrecord Always [src]
  Trigger
  (ready [this]
    (let [vs (chan)]
      (go-loop []
        (>! vs (set-ready (<! (versions src))))
        (recur))
      vs)))
