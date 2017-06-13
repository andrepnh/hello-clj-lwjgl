(ns hello-clj-lwjgl.core
  (:import (org.lwjgl.glfw Callbacks GLFW GLFWErrorCallback GLFWVidMode GLFWKeyCallbackI))
  (:import (org.lwjgl Version))
  (:import (org.lwjgl.opengl GL GL11))
  (:import (org.lwjgl.system MemoryStack MemoryUtil))
  (:import (java.nio IntBuffer))
  (:gen-class))

(def window (atom 0))

(defn init []
  (doto
    (GLFWErrorCallback/createPrint System/err)
    (.set))
  (when-not (GLFW/glfwInit)
    (throw (.IllegalStateException "Unable to initialize GLFW")))
  (GLFW/glfwDefaultWindowHints)
  (GLFW/glfwWindowHint GLFW/GLFW_VISIBLE GLFW/GLFW_FALSE)
  (GLFW/glfwWindowHint GLFW/GLFW_RESIZABLE GLFW/GLFW_TRUE)
  (reset! window (GLFW/glfwCreateWindow 300 300 "Hello World!" MemoryUtil/NULL MemoryUtil/NULL))
  (when (= @window MemoryUtil/NULL)
    (throw (.IllegalStateException "Failed to create the GLFW window")))
  (GLFW/glfwSetKeyCallback
    @window
    (reify GLFWKeyCallbackI (invoke [this win key _ action mods]
      (when (and (= key GLFW/GLFW_KEY_ESCAPE) (= action GLFW/GLFW_RELEASE))
        (GLFW/glfwSetWindowShouldClose @window true)))))
  (with-open [stack (MemoryStack/stackPush)]
    (let [pWidth (.mallocInt stack 1)
          pHeight (.mallocInt stack 1)]
      (do (GLFW/glfwGetWindowSize @window pWidth pHeight)
          (let [vidmode (GLFW/glfwGetVideoMode (GLFW/glfwGetPrimaryMonitor))]
            (GLFW/glfwSetWindowPos @window
                                   (/ (- (.width vidmode) (.get pWidth 0)) 2)
                                   (/ (- (.height vidmode) (.get pHeight 0)) 2))))))
  (GLFW/glfwMakeContextCurrent @window)
  (GLFW/glfwSwapInterval 1)
  (GLFW/glfwShowWindow @window))

(defn gloop []
  (GL/createCapabilities)
  (GL11/glClearColor 1.0 1.0 0.0 0.0)
  (while (not (GLFW/glfwWindowShouldClose @window))
    (do
      (GL11/glClear (bit-or GL11/GL_COLOR_BUFFER_BIT GL11/GL_DEPTH_BUFFER_BIT))
      (GLFW/glfwSwapBuffers @window)
      (GLFW/glfwPollEvents))))

(defn run []
  (println "Hello LWJGL " (Version/getVersion) "!")
  (init)
  (gloop)
  (Callbacks/glfwFreeCallbacks @window)
  (GLFW/glfwDestroyWindow @window)
  (GLFW/glfwTerminate)
  (doto (GLFW/glfwSetErrorCallback nil) (.free)))

(defn -main
  "Application entry point"
  [& args]
  (run))
