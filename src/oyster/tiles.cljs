(ns oyster.tiles)

(def all-tiles
  {:water {:char \~
           :style {:color "blue"}
           :description "Water."}

   :grass {:char \,
           :style {:color "green"}
           :description "Grass."}

   :tilled-soil {:char \=
                 :style {:color "brown"}
                 :description "Tilled soil."}

   :radish-seed {:char "&bull;"
                 :style {:color "brown"}
                 :description "A radish seed is planted here."}

   :radish {:char \u2660
            :style {:color "grey"}
            :description "A fully grown radish."}

   :beach {:char \.
           :style {:color "orange"}
           :description "A sandy beach."}

   :nothing {:char \space
             :description "Nothing."}

   })
