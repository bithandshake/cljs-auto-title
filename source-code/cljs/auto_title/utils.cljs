
(ns auto-title.utils
    (:require [auto-title.side-effects :as side-effects]
              [auto-title.state        :as state]
              [reagent.api             :as reagent]
              [time.api                :as time]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn title-sensor-did-mount-f
  ; @ignore
  ;
  ; @param (keyword) sensor-id
  ; @param (map) sensor-props
  [sensor-id sensor-props]
  ; When a 'sensor' component mounts into the React tree, the previous one might
  ; just unmounting with an ongoing disappearing effect.
  ; Therefore the currently mounted one has to wait before it applies the 'set-title!'
  ; function, otherwise the disappearing title might changes in the last moments
  ; of disappearing.
  (letfn [(f [] (side-effects/set-title! sensor-id sensor-props))]
         (time/set-timeout! f 150))
  (swap! state/SENSORS assoc sensor-id sensor-props)
  (side-effects/setup-intersection-observer! sensor-id sensor-props))

(defn title-sensor-will-unmount-f
  ; @ignore
  ;
  ; @param (keyword) sensor-id
  ; @param (map) sensor-props
  [sensor-id sensor-props]
  (swap! state/SENSORS dissoc sensor-id)
  (side-effects/remove-intersection-observer! sensor-id sensor-props))

(defn title-sensor-did-update-f
  ; @ignore
  ;
  ; @param (keyword) sensor-id
  ; @param (?) %
  [sensor-id %]
  (let [[_ sensor-props] (reagent/arguments %)]
       (side-effects/update-title! sensor-id sensor-props)))
