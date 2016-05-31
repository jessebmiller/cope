(ns cope.views
  (:require [clojure.core :refer :all]
            [clojure.core.async :refer [go-loop <! <!!]]
            [cope.build_processes :refer [build]]))

(defprotocol View
  "A means to show artifacts to an audience"
  (view [this] "a view of an artifact"))

;; AtomView updates an atom with the most recent artifact
(defrecord AtomView [build-process]
  View
  (view [this]
    (let [artifacts (build build-process)
          latest-artifact (atom (:artifact (<!! artifacts)))]
      (go-loop [art @latest-artifact]
        (if art
          (let [next-art (:artifact (<! artifacts))]
            (reset! latest-artifact next-art)
            (recur next-art))
          "Done"))
      latest-artifact)))
