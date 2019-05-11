(ns usb-descriptor.core-test
    (:require [clojure.test :refer :all]
              [usb-descriptor.core :as usb]
              [clojure.spec-alpha2 :as s]
              [encode-binary.core :as e]))

(def example-device
  #:usb{:bLength 

(deftest standard-descriptor)
