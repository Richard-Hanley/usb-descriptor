(ns usb-descriptor.hid
  (:require [encode-binary.core :as e])
  (:require [usb-descriptor.core :as usb]
            [clojure.spec-alpha2 :as s]
            [clojure.set :as set]))

(usb/defusb ::hid 0x21
  :fields [(s/def ::bcdHID ::usb/bcd)
           (s/def ::bCountryCode ::e/uint8)
           (s/def ::bNumDescriptors ::e/uint8)
           (s/def ::descriptors 
             (e/array (e/struct :fields [(s/def ::bDescriptorType ::e/uint8)
                                           (s/def ::wDescriptorLength ::e/uint16)])))]
  :deps [(e/count-dependency ::bNumDescriptors ::descriptors)])

