(ns usb-descriptor.core 
  (:require [encode-binary.core :as e])
  (:require [clojure.spec-alpha2 :as s]
            [clojure.set :as set]))

(defmulti standard-descriptor ::bDescriptorType)

(s/def ::standard-descriptor 
  (e/multi-codec standard-descriptor 
                 ::bDescriptorType
                 :decoder-tag second))


(defmacro defusb
  [typename typevalue codec]
  `(e/symbolic-method standard-descriptor ~typename ~typevalue ~codec))

(s/def ::bLength ::e/uint8)
(s/def ::bDescriptorType ::e/uint8)
(s/def ::bcd ::e/uint16)

(defusb ::device 1 
  (e/struct :fields [::bLength
                     ::bDescriptorType
                     (s/def ::bcdUSB ::bcd)
                     (s/def ::bDeviceClass ::e/uint8)
                     (s/def ::bDeviceSubClass ::e/uint8)
                     (s/def ::bDeviceProtocol ::e/uint8)
                     (s/def ::bMaxPacketSize (e/specify ::e/uint8
                                                        (s/spec #{8 16 32 64})))
                     (s/def ::idVendor ::e/uint16)
                     (s/def ::idProduct ::e/uint16)
                     (s/def ::bcdDevice ::bcd)
                     (s/def ::iManufacturer ::e/uint8)
                     (s/def ::iProduct ::e/uint8)
                     (s/def ::iSerialNumber ::e/uint8)
                     (s/def ::bNumConfigurations ::e/uint8)]))






