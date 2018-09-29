(ns demo.gatsby
    (:require
        [clojure.java.io :as io]))

(defmacro with-query
  "Binds the data produced by the GraphQL query to the name and
   executes the element within the binding's context.
   Use goog.object/getValueByKeys to access the resulting #js data.

   Ex.: (with-query \"query SiteTitleQuery { site { siteMetadata { title }}}\" data
          [:h3 (obj/getValueByKeys data \"site\" \"siteMetadata\" \"title\")])

   Known limitations: The query is not processed by relay-compiler and thus supports
                      only the GraphQL standard without any extensions.
  "
  [query name element]
  (let [sha (str "sha" (hash query))
        file (str "/tmp/cljs-queries/" sha ".query")]
    ;; Write the query into a file so that it can be resolved later,
    ;; use its sha in place of the data that will be inserted there later
    (io/make-parents file)
    (spit file query)
    ;; Note: The data will be #js but after compilation to js so we
    ;; don't need to tag it a such
    `(let [~name ~(str "#GRAPHQL:" sha ":GRAPHQL#")]
       (if (string? ~name)
         [:h3 "Loading..."]
         ~element))))
