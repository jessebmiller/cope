(ns cope.core-test
  (:require [clojure.test :refer :all]
            [clojure.core.async :refer [chan <!!]]
            [cope.core :refer [making showing]]
            [cope.sources :refer [Source versions]]
            [cope.triggers :refer [Trigger ready]]
            [cope.build_processes :refer [BuildProcess build]])
  (:import cope.sources.ConstSrc
           cope.triggers.Always
           cope.build_processes.Passthrough))

(deftest test-can-pass
  (testing "prove tests are running and can pass"
    (is (= true true))))

(deftest test-const-src
  (testing "ConstSrc is a trivial source of strings for testing"
    (let [str-version (versions (ConstSrc. "mock source string"))
          map-version (versions (ConstSrc. {:const true}))]
      (is (= (<!! str-version) {:source "mock source string"}))
      (is (= (<!! map-version) {:source {:const true}}))
      (is (= (<!! map-version) {:source {:const true}})))))

(deftest test-immediate-trigger
  (testing "Always triggers imediately for every new version"
    (let [ready-versions (ready (Always. (ConstSrc. "mock-source")))]
      (is (= (<!! ready-versions) {:ready true :source "mock-source"}))
      (is (= (<!! ready-versions) {:ready true :source "mock-source"}))
      (is (= (<!! ready-versions) {:ready true :source "mock-source"}))
      (is (= (<!! ready-versions) {:ready true :source "mock-source"}))
      (is (= (<!! ready-versions) {:ready true :source "mock-source"})))))

(deftest test-null-pipeline
  (testing "null-pipeline should build artifacts identical to the source values"
    (let [artifacts (build (Passthrough. (Always. (ConstSrc. "MockSrc"))))]
      (is (= (<!! artifacts)
             {:ready true :source "MockSrc" :artifact "MockSrc"})
          (= (<!! artifacts)
             {:ready true :source "MockSrc" :artifact "MockSrc"})))))

(deftest test-return-to-caller-view
  (testing "return-to-caller-view should show (return) the artifact to a calling
            function"
    (is false)))

(deftest test-callback-feedback
  (testing "callback-feedback should use a callback function as feedback"
    (is false)))
