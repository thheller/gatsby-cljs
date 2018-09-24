(ns shadow.gatsby
  (:require [clojure.java.io :as io]
            [cljs.compiler :as cljs-comp]
            [clojure.string :as str]))

(defn all-vars [state]
  (for [[ns ns-info] (get-in state [:compiler-env :cljs.analyzer/namespaces])
        ns-def (-> ns-info :defs vals)]
    ns-def))

(defn create-pages
  {:shadow.build/stage :flush}
  [state]
  (doseq [ns-def (all-vars state)
          :when (get-in ns-def [:meta :gatsby/page])]

    (let [{:gatsby/keys [page graphql]}
          (:meta ns-def)

          page-ns
          (-> ns-def :name namespace cljs-comp/munge)

          page-var
          (-> ns-def :name name cljs-comp/munge)

          content
          (str
            (when graphql
              "import { graphql } from \"gatsby\";")

            "\nexport {" page-var " as default} from \"../cljs/" page-ns ".js\";"
            (when graphql
              (str "\nexport const query = graphql`" graphql "`;")))

          out-dir
          (io/file "site" "src" "pages")

          out-file
          (io/file out-dir (str page ".js"))]

      (io/make-parents out-file)
      (spit out-file content)
      ))
  state)