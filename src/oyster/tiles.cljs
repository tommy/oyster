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

   :radish-seed {:char \u2248
                 :style {:color "brown"}
                 :description "A radish seed is planted here."}

   :beach {:char \.
           :style {:color "orange"}
           :description "A sandy beach."}

   :nothing {:char \space
             :description "Nothing."}

   })
