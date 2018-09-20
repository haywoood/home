(ns dollars.core
  (:use [debux.core])
  (:require [clojure.java.io :as io]
            [dollars.css :refer [styles]]
            [rum.core :as rum]))

;; Helpers

(def out-dir "./resources/")
(def img-dir (str out-dir "img"))

(defn trim-img-path [path]
  (clojure.string/replace path #"./resources" ""))

(defn img-name [img-path]
  (clojure.string/replace img-path
                          #"/img/|\.jpg|\.gif"
                          ""))

(defn write-html
  ([content]
   (write-html "index.html" content))
  ([file-name content]
   (spit (str out-dir file-name)
         (rum/render-html content))))

;; Data

(def IMAGES (->> (io/file img-dir)
                 file-seq
                 (filter #(.isFile %))
                 (mapv #(-> % str trim-img-path))))

;; Components

(rum/defc site-header []
  [:div {:style {:margin-bottom    10
                 :display          :inline-block
                 :color            "#0b31ff"
                 :background-color "#2effec"}}
   "ryan haywood"])

(rum/defc image-nav [active]
  (for [image IMAGES]
    (let [name (img-name image)
          href (str "/drawings/" name ".html")]
      [:div
       [:a {:class (str ""
                        (when (= active name) "active"))
            :href  href} name]])))

(rum/defc main-layout [{:keys [active]} & children]
  [:html
   [:head
    [:style styles]]
   [:body
    [:div {:style {:display :flex
                   :flex    1}}
     [:div {:style {:min-width   192
                    :margin-left 5
                    :margin-top  5}}
      (site-header)
      (image-nav active)]
     [:div {:style {:display :flex :flex 1}}
      [:div {:style {:display         :flex
                     :flex            1
                     :align-items     :center
                     :justify-content :center}}
       children]
      [:div {:style {:width 192}}
       ;;content goes here for img
       ]]]]])

(rum/defc page [{:keys [img]}]
  [:div
   [:img {:style {:height "97%"
                  :width  :auto}
          :src   img}]])

;; Go

(comment
  (do
    ;; make home page
    (let [n (rand-int (count IMAGES))]
      (write-html
       (main-layout {:active (img-name (nth IMAGES n))}
                    (page {:img (nth IMAGES n)}))))

    ;; gen pages for each image
    (for [img IMAGES]
      (let [name (img-name img)]
        (write-html
         (str "drawings/" name ".html")
         (main-layout {:active name}
                      (page {:img img}))))))
  )
