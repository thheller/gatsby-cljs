(ns demo.gatsby
  (:require [reagent.core :as r]))

;; can include images+css this way
(js/require "../css/layout.css")

(defn page-index
  {:export true
   :gatsby/page "index"
   :gatsby/graphql
   "query IndexQuery {
      site {
        siteMetadata {
          description
        }
      }
   }"}
  [props]
  (r/as-element
    [:h1 "hello world!"]))

(defn page-404
  {:export true
   :gatsby/page "404"}
  [props]
  (r/as-element
    [:h1 "404!"]))