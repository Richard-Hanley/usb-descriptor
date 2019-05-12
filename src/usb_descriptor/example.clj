(ns usb-descriptor.example
    (:require [clojure.test :refer :all]
              [usb-descriptor.core :as usb]
              [usb-descriptor.hid :as hid]
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
        :bNumConfigurations 4})

(def example-config
  #::usb{:bDescriptorType ::usb/config
         :wTotalLength 125
         :bNumInterfaces 3
         :bConfigurationValue 0
         :iConfiguration 0
         :bmAttributes 0xe0
         :bMaxPower 100})

(def example-interface
  #::usb{:bInterfaceNumber 1
         :bAlternateSetting 0
         :bNumEndpoints 0
         :bInterfaceClass 4
         :bInterfaceSubClass 26
         :bInterfaceProtocol 9
         :iInterface 1})

(def example-endpoint
  #::usb{:bDescriptorType ::usb/endpoint
         :bEndpointAddress 0x81
         :bmAttribute 0x12
         :wMaxPacketSize 64
         :bInterval 1})

(def example-huge-endpoint
  #::usb{:bDescriptorType ::usb/endpoint
         :bEndpointAddress 0x81
         :bmAttribute 0x12
         :wMaxPacketSize 1024
         :bInterval 1})

(def example-hid
  #::hid{::usb/bDescriptorType ::hid/hid
         :bcdHID 0x101
         :bCountryCode 0
         :bNumDescriptors 0
         :descriptors [#::hid{:bDescriptorType 0x22 :wDescriptorLength 145}]})

