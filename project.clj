(require 'leiningen.core.eval)

;; per-os jvm-opts code cribbed from Overtone
(def JVM-OPTS
  {:common   []
   :macosx   []
   :linux    []
   :windows  []})

(defn jvm-opts
  "Return a complete vector of jvm-opts for the current os."
  [] (let [os (leiningen.core.eval/get-os)]
       (vec (set (concat (get JVM-OPTS :common)
                   (get JVM-OPTS os))))))

(def lwjgl-ns "org.lwjgl")

(def lwjgl-version "3.1.2")

(def lwjgl-modules ["lwjgl"
                    "lwjgl-glfw"
                    "lwjgl-jemalloc"
                    "lwjgl-openal"
                    "lwjgl-opengl"
                    "lwjgl-stb"])

(def lwjgl-platforms ["linux" "macos" "windows"])

;; These packages don't have any associated native ones.
(def no-natives? #{"lwjgl-egl" "lwjgl-jawt" "lwjgl-opencl" "lwjgl-vulkan"})

(defn lwjgl-deps-with-natives []
  (apply concat
    (for [m lwjgl-modules]
      (let [prefix [(symbol lwjgl-ns m) lwjgl-version]]
        (into [prefix]
          (if (no-natives? m)
            []
            (for [p lwjgl-platforms]
              (into prefix [:classifier (str "natives-" p)
                            :native-prefix ""]))))))))

(def all-dependencies
  (into
    '[[org.clojure/clojure "1.8.0"]]
    (lwjgl-deps-with-natives)))

(defproject hello-clj-lwjgl "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies ~all-dependencies
  :main ^:skip-aot hello-clj-lwjgl.core
  :target-path "target/%s"
  :jvm-opts ^:replace ~(jvm-opts)
  :profiles {:uberjar {:aot :all}})
