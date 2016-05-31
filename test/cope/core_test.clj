(ns cope.core-test
  (:require [clojure.test :refer :all]
            [clojure.core.async :refer [chan <!!]]
            [cope.core :refer [making showing]]
            [cope.sources :refer [Source versions]]
            [cope.triggers :refer [Trigger ready]]
            [cope.build_processes :refer [BuildProcess build]]
            [cope.views :refer [View view]])
  (:import cope.sources.ConstSrc
           cope.sources.SingleSrc
           cope.triggers.Always
           cope.build_processes.Passthrough
           cope.views.AtomView))

(deftest test-can-pass
  (testing "prove tests are running and can pass"
    (is (= true true))))

(deftest test-const-src
  (testing "ConstSrc is a trivial source of strings for testing"
    (let [str-versions (versions (ConstSrc. "mock source string"))
          map-versions (versions (ConstSrc. {:const true}))]
      (is (= (<!! str-versions) {:source "mock source string"}))
      (is (= (<!! map-versions) {:source {:const true}}))
      (is (= (<!! map-versions) {:source {:const true}})))))

(deftest test-always-trigger
  (testing "Always triggers imediately for every new version"
    (let [ready-versions (ready (Always. (ConstSrc. "mock-source")))]
      (is (= (<!! ready-versions) {:ready true :source "mock-source"}))
      (is (= (<!! ready-versions) {:ready true :source "mock-source"}))
      (is (= (<!! ready-versions) {:ready true :source "mock-source"}))
      (is (= (<!! ready-versions) {:ready true :source "mock-source"}))
      (is (= (<!! ready-versions) {:ready true :source "mock-source"})))))

(deftest test-passthrough-bildtool
  (testing "Passthrough should build artifacts identical to the source values"
    (let [artifacts (build (Passthrough. (Always. (ConstSrc. "MockSrc"))))]
      (is (= (<!! artifacts)
             {:ready true :source "MockSrc" :artifact "MockSrc"})
          (= (<!! artifacts)
             {:ready true :source "MockSrc" :artifact "MockSrc"})))))

(deftest test-single-src
  (testing "SingleSrc is a trivial source of a single value"
    (let [str-version (versions (SingleSrc. "Single Mock"))]
      (is (= (<!! str-version) {:source "Single Mock"}))
      (is (= (<!! str-version) nil)))))

(deftest test-atom-view
  (testing "AtomView provides access to artifacts in an atom"
    (let [current-artifact
          (view (AtomView. (Passthrough. (Always. (SingleSrc. "Show Mock")))))]
      (is (= @current-artifact "Show Mock")))))

(deftest test-callback-feedback
  (testing "callback-feedback should use a callback function as feedback"
    (is false)))
