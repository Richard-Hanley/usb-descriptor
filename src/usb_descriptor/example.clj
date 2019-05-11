(ns usb-descriptor.example
    (:require [clojure.test :refer :all]
              [usb-descriptor.core :as usb]
              [clojure.spec-alpha2 :as s]
              [encode-binary.core :as e]))

(def example-device
  #::usb{:bLength 18
        :bDescriptorType 1
        :bcdUSB 0x0200
        :bDeviceClass 0
        :bDeviceSubClass 0
        :bDeviceProtocol 0
        :bMaxPacketSize 64
        :idVendor 0x1038
        :idProduct 0xBEEF
        :bcdDevice 0x0315
        :iManufacturer 1
        :iProduct 2
        :iSerialNumber 0
        :bNumConfigurations 1})

