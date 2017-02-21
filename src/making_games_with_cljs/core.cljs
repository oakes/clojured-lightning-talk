(ns making-games-with-cljs.core
  (:require [play-cljs.core :as p]
            [making-games-with-cljs.state :as s]
            [making-games-with-cljs.utils :as u]
            [goog.events :as events]))

(defonce game (p/create-game u/view-size u/view-size))
(defonce state (atom nil))

(defn smiley []
  [:fill {:color "yellow"}
   [:ellipse {:width 100 :height 100}
    [:fill {:color "black"}
     [:ellipse {:x -20 :y -10 :width 10 :height 10}]
     [:ellipse {:x 20 :y -10 :width 10 :height 10}]]
    [:fill {}
     [:arc {:width 60 :height 60 :start 0 :stop 3.14}]]]])

(def raw-slides
  [[:text {:value "Making Games at Runtime\nwith ClojureScript" :halign :center}]
   [[:text {:value "play-clj" :halign :center}]
    [:text {:value (str "My old library" \newline
                     "Based on libGDX (runs on JVM)" \newline
                     "Uses tons of macros, opinionated about state")
            :halign :center :size 16 :y 50}]]
   [[:text {:value "play-cljs" :halign :center}]
    [:text {:value (str "My new library" \newline
                     "Based on p5.js (runs in browsers)" \newline
                     "Data-oriented, not opinionated about state")
            :halign :center :size 16 :y 50}]]
   [[:text {:value "What is p5.js?" :halign :center}]
    [:text {:value (str "Pure JS variant of Processing" \newline
                     "Very beginner-friendly")
            :halign :center :size 16 :y 50}]]
   [[:text {:value "p5.js example" :halign :center :y -50}]
    [:div {:x 200 :y 50}
     (smiley)]
    [:text {:value "fill(\"yellow\");
ellipse(50, 50, 100, 100);
fill(\"black\");
ellipse(30, 40, 10, 10);
ellipse(70, 40, 10, 10);
noFill();
arc(50, 55, 60, 60, 0, 3.14);"
            :font "Courier New" :halign :left :size 14 :x -150}]]
   [[:text {:value "play-cljs example" :halign :center :y -50}]
    [:text {:value "(render
  [:fill {:color \"yellow\"}
   [:ellipse {:width 100 :height 100}
    [:fill {:color \"black\"}
     [:ellipse {:x -20 :y -10 :width 10 :height 10}]
     [:ellipse {:x 20 :y -10 :width 10 :height 10}]]
    [:fill {}
     [:arc {:width 60 :height 60 :start 0 :stop 3.14}]]]])"
            :font "Courier New" :halign :left :size 14 :x -150}]]
   [[:text {:value "Try it out!" :halign :center}]
    [:text {:value (str "https://github.com/oakes/play-cljs" \newline
                     \newline
                     "Built-in template on Nightcoders.net")
            :halign :center :size 16 :y 50}]]])

(def slides
  (reduce
    (fn [nested-slides slide]
      [:div {:x u/view-size} slide nested-slides])
    []
    (reverse raw-slides)))

(def main-screen
  (reify p/Screen
    (on-show [_]
      (when-not @state
        (reset! state (s/initial-state game))))
    (on-hide [_])
    (on-render [_]
      (let [{:keys [x y current]} @state]
        (p/render game [[:stroke {}
                         [:fill {:color "lightblue"}
                          [:rect {:width u/view-size :height u/view-size}]]]
                        [:tiled-map {:value (:map @state) :x x}]
                        [:div {:x (- (+ x 350)) :y 100}
                         slides]
                        [:div {:x u/koala-offset :y y}
                         current]]))
      (reset! state
        (-> @state
            (s/move game)
            (s/prevent-move game)
            (s/animate))))))

(doto game
  (p/start)
  (p/set-screen main-screen))

