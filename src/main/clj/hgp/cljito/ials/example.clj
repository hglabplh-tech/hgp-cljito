(ns hgp.cljito.ials.example)

(defn huhu [x y ]
  (+ x y ))

(println (symbol? (get (meta (var huhu) ) :name) ))
