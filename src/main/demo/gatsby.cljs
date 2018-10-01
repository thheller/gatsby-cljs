(ns demo.gatsby
  (:require
    [reagent.core :as r]
    [goog.object :as obj]
    ["react-helmet" :default Helmet]
    ["gatsby" :as g])
  (:require-macros [demo.gatsby :refer [with-query]]))

;; can include images+css this way
(js/require "../css/layout.css")

(defn header [{:keys [title] :as props}]
  [:div {:style {:background "rebeccapurple"
                 :margin-bottom "1.45rem"}}
   [:div {:style {:margin "0 auto"
                  :max-width 960
                  :padding "1.45rem 1.0875rem"}}
    [:h1 {:style {:margin 0}}
     ;; only using my helper fn since I have no clue how to do it in pure react
     [:> g/Link {:to "/" :style {:color "white" :textDecoration "none"}} title]]]])

(defn layout* [props & body]
  (r/as-element
    [:<>
     [:> Helmet {:title "hello world" :meta []}]
     (header {:title (obj/getValueByKeys (:data props) "site" "siteMetadata" "title")})
     (into
       [:div.page-content {:style {:margin "0 auto"
                                   :max-width 960
                                   :padding "0px 1.0875rem 1.45rem"
                                   :paddingTop 0}}]
       body)]))

(defn layout [props & body]
  ;; gatsby wants to extract the graphql from the AST but doesn't understand CLJS code
  ;; so we can't use g/StaticQuery and must use our own with-query instead
  (with-query
      "query SiteTitleQuery { site { siteMetadata { title }}}"
      data
      (apply layout* (assoc props :data data) body)))

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
  (layout {}
    [:h1 "Hi people!"]

    [:p "Welcome to your new Gatsby site."]
    [:p "Now go build something great with ClojureScript."]

    [:> g/Link {:to "/page-2/"} "Go to page 2"]))

(defn page-2
  {:export true
   :gatsby/page "page-2"}
  [props]
  (layout {}
    [:h2 "Hi from the second page"]
    [:p "Welcome to page 2"]

    [:> g/Link {:to "/"} "Go back to the homepage"]))

(defn page-404
  {:export true
   :gatsby/page "404"}
  [props]
  (r/as-element
    [:h1 "404!"]))
