(ns cope.build_processes
  (:require [clojure.core.async :refer [chan go-loop <! >!]]
            [cope.triggers :refer [ready]]))

(defprotocol BuildProcess
  "A process that takes source to an artifact"
  (build [this] "chan of built artifacts"))

;; The empty build process. One which passes the source through unchanged
(defrecord Passthrough [trigger]
  BuildProcess
  (build [this]
    (let [as (chan)]
      (go-loop []
        (>! as (let [src (<! (ready trigger))]
                 ;; set :artifact to be the same as :source in src
                 (assoc src :artifact (:source src))))
        (recur))
      as)))
