(ns cope.sources
  (:require [clojure.core.async :refer [go go-loop chan close! <!! >!]]))

(defn- version [source-value]
  "wrap a source value in version metadata"
  {:source source-value})

(defprotocol Source
  "A stream of versions of a source as it developes"
  (versions [this] "chan of versions"))

;; SingleSrc is a trivial source of a single value
(defrecord SingleSrc [val]
  Source
  (versions [this]
    (let [v (chan)]
      (go
        (>! v (version val))
        (close! v))
      v)))

;; ConstSrc is a trivial Source whose versions are some unchanging constant
(defrecord ConstSrc [val]
  Source
  (versions [this]
    (let [vs (chan)]
      (go-loop [] (>! vs (version val)) (recur))
      vs)))
