(ns usb-descriptor.core 
  (:require [encode-binary.core :as e])
  (:require [clojure.spec-alpha2 :as s]
            [clojure.set :as set]))

(defmulti standard-descriptor ::bDescriptorType)

(s/def ::standard-descriptor 
  (e/multi-codec standard-descriptor 
                 ::bDescriptorType
                 :decoder-tag second))

(s/def ::bLength ::e/uint8)
(s/def ::bDescriptorType ::e/uint8)
(s/def ::bcd ::e/uint16)

(defn make-standard-struct
  "Given a list of fields, this will make sure that the first
  two fields are ::bLength and ::bDescriptorType.

  If all of the fields are a fixed length, it will also
  force the length to be the correct size"
  [typename typevalue fields deps]
  (let [fields-with-header (distinct (concat [::bLength ::bDescriptorType] fields))
        fixed-size (or (reduce (fn [a v] (if-let [size (e/sizeof v)]
                                           (+ a size)
                                           (reduced nil)))
                               0
                               fields-with-header)
                       0)
        ;The fixed length may or may not be nil
        ;but this spec will ensure that the key exists
        fixed-length (s/spec* `(e/constant-field ::bLength ~fixed-size))
        fixed-type (s/spec* `(e/constant-field ::bDescriptorType ~typevalue))
        struct-codec (e/struct :fields fields-with-header :deps deps)
        get-length `(fn [value#]
                     (e/sizeof (e/encode ~typename value#)))]
    (if (pos? fixed-size)
      (e/specify fixed-length
                 fixed-type 
                 struct-codec)
      (e/specify fixed-type 
                 fixed-length
                 struct-codec
                 (s/spec* `(e/dependent-field ::bLength ~get-length))))))

(defmacro defusb
  [typename typevalue & {:keys [fields deps]}]
  `(do
     (s/def ~typename (make-standard-struct ~typename ~typevalue ~fields ~deps))
     (e/symbolic-method standard-descriptor ~typename ~typevalue ~typename)))


(defusb ::device 1 
  :fields [::bLength
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
           (s/def ::bNumConfigurations ::e/uint8)])

(defusb ::config 2
  :fields [(s/def ::wTotalLength ::e/uint16)
           (s/def ::bNumInterfaces ::e/uint8)
           (s/def ::bConfigurationValue ::e/uint8)
           (s/def ::iConfiguration ::e/uint8)
           (s/def ::bmAttributes ::e/uint8)
           (s/def ::bMaxPower ::e/uint8)])

(defusb ::string 3
  :fields [(s/def ::bString ::e/utf-16)])

(defusb ::interface 4
  :fields [(s/def ::bInterfaceNumber ::e/uint8)
           (s/def ::bAlternateSetting ::e/uint8)
           (s/def ::bNumEndpoints ::e/uint8)
           (s/def ::bInterfaceClass ::e/uint8)
           (s/def ::bInterfaceSubClass ::e/uint8)
           (s/def ::bInterfaceProtocol ::e/uint8)
           (s/def ::iInterface ::e/uint8)])

(defusb ::endpoint 5
  :fields [(s/def ::bEndpointAddress ::e/uint8)
           (s/def ::bmAttribute ::e/uint8)
           (s/def ::wMaxPacketSize ::e/uint16)
           (s/def ::bInterval ::e/uint8)])

(def core-usb-types #{1 2 3 4 5})

(s/def ::class (e/specify ::standard-descriptor
                          (s/spec #(not (contains? core-usb-types (::bDescriptorType %))))))


(s/def ::force-endpoint
  (s/conformer #(assoc-in % [:interface ::bNumEndpoints]
                          (count (:endpoints %)))))

(s/def ::interface-coll (e/specify (e/cat :fields [:interface ::interface
                                                   :classes (e/* ::class :while (fn [bin] 
                                                                                  (not (contains? core-usb-types (second bin)))))
                                                   :endpoints (e/* ::endpoint :while (comp #{5} second))])
                                   ::force-endpoint))

(s/def ::force-interface
  (s/conformer #(assoc-in % [:config ::bNumInterfaces]
                          (count (:interfaces %)))))

(s/def ::config-coll (e/specify (e/cat :fields [:config ::config
                                                :interfaces (e/* ::interface-coll :while (comp #{4} second))])
                                ::force-interface))

(s/def ::force-config
  (s/conformer #(assoc-in % [:device ::bNumConfigurations]
                          (count (:configs %)))))

(s/def ::device-coll (e/specify (e/cat :fields [:device ::device
                                                :configs (e/* ::config-coll :while (comp #{2} second))])
                                ::force-config))

