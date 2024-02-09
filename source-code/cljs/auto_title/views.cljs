
(ns auto-title.views
    (:require [auto-title.utils  :as utils]
              [fruits.css.api    :as css]
              [fruits.hiccup.api :as hiccup]
              [fruits.random.api :as random]
              [reagent.core      :as reagent]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn sensor
  ; @param (keyword)(opt) sensor-id
  ; @param (map) sensor-props
  ; {:offset (px)(opt)
  ;  :title (metamorphic-content)(opt)
  ;  :title-placeholder (metamorphic-content)(opt)}
  ;
  ; @usage
  ; [sensor {...}]
  ;
  ; @usage
  ; [sensor :my-sensor {...}]
  ([sensor-props]
   [sensor (random/generate-keyword) sensor-props])

  ([sensor-id {:keys [offset] :as sensor-props}]
   ; The following cases might occur:
   ; 1. Multiple 'sensor' components are mounted into the React tree.
   ; 2. A 'sensor' component unmounts right after when the next one has been mounted into the React tree.
   ; 3. A 'sensor' component updates after it has been mounted into the React tree (e.g., the title changes).
   ; 4. A 'sensor' component mounts into the React tree outside of the viewport.
   ; 5. A 'sensor' component mounts into the React tree inside the viewport.
   (reagent/create-class {:component-did-mount    (fn []  (utils/title-sensor-did-mount-f    sensor-id sensor-props))
                          :component-will-unmount (fn []  (utils/title-sensor-will-unmount-f sensor-id sensor-props))
                          :component-did-update   (fn [%] (utils/title-sensor-did-update-f   sensor-id %))
                          :reagent-render         (fn []  (let [element-id (hiccup/value sensor-id "auto-title-sensor")]
                                                               [:div {:style (if offset {:transform (-> offset css/px css/translate-y)})
                                                                      :id element-id}]))})))
