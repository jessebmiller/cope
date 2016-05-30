(ns cope.sources
  (:require [clojure.core.async :as async :refer [go-loop chan <!! >!]]))

(defn- version [source-value]
  "wrap a source value in version metadata"
  {:source source-value})

(defprotocol Source
  "A stream of versions of a source as it developes"
  (versions [this] "chan of versions"))

;; ConstSrc is a trivial Source whose versions are some unchanging constant
(defrecord ConstSrc [val]
  Source
  (versions [this]
    (let [vs (chan)]
      (go-loop [] (>! vs (version val)) (recur))
      vs)))
