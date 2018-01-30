(ns gen.dom)


(defn create-element [type]
  (.createElement js/document type))


(defn append [parent el]
  (.appendChild parent el))


(defn prepend [parent el]
  (.insertBefore parent el (.-firstChild parent)))


(def body (.-body js/document))


(defn $ [sel] (.querySelector body sel))