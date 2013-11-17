(ns oyster.random)

(defn rng
  "A random number generator seeded with the supplied seed,
  or autoseeded if none is given."
  ([]
   (let [seed (.seedrandom js/Math)]
     (print "Using seed:" seed)
     (.-random js/Math)))
  ([seed]
   (let [seed (.seedrandom js/Math seed)]
     (print "Using seed:" seed)
     (.-random js/Math))))

(defn rand-int
  "Generate a random integer between a (inclusive, default 0) and b (exclusive)
  using the given random number generator."
  ([rng b]
  (int (* b (rng))))
  ([rng a b]
  (let [d (- b a)]
    (+ a (rand-int rng d)))))
