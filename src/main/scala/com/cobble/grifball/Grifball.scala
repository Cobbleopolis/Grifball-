package com.cobble.grifball

import java.io.File
import java.nio.{ByteBuffer, FloatBuffer}

import org.lwjgl.BufferUtils
import org.lwjgl.glfw._
import org.lwjgl.glfw.GLFW._
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._
import org.lwjgl.system.MemoryUtil

object Grifball {

    var glfwErrorCallback: GLFWErrorCallback = _
    var windowId: Long = _

    val WIDTH: Int = 1080
    val HEIGHT: Int = 720
    val TITLE: String = "Grifball"

    var keyCallback: GLFWKeyCallback = _

    var (r, g, b, a): (Float, Float, Float, Float) = (0.4f, 0.6f, 0.9f, 0f)
    var (rd, gd, bd, ad): (Int, Int, Int, Int) = (1, 1, 1, 1)

    var vaoId: Int = _

    var vboId: Int = _

    var vboiId: Int = _

    val verts: Array[Float] = Array[Float](
        -0.5f,  0.5f, 0f, // Left top         ID: 0
        -0.5f, -0.5f, 0f, // Left bottom      ID: 1
         0.5f, -0.5f, 0f, // Right bottom     ID: 2
         0.5f,  0.5f, 0f  // Right left       ID: 3
    )

    val inds: Array[Byte] = Array[Byte](
        // Left bottom triangle
        0, 1, 2,
        // Right top triangle
        2, 3 ,0
    )

	def main(args: Array[String]): Unit = {
		println("Boo motherfucker!")
        setNatives()
        initWindow()
        initGL()
        loop()
	}

    def setNatives(): Unit = {
        //        val os = System.getProperty("os.name").toLowerCase
        //        var suffix = ""
        //        if (os.contains("win")) {
        //            suffix = "windows"
        //        } else if (os.contains("mac")) {
        //            suffix = "macosx"
        //        } else {
        //            suffix = "linux"
        //        }
        //        val nativePath = System.getProperty("user.dir") + File.separator + "lib" + File.separator + "lwjgl" + File.separator + "native" + File.separator + suffix
        val nativePath = System.getProperty("user.dir") + File.separator + "target" + File.separator + "natives"
        System.setProperty("org.lwjgl.librarypath", nativePath)
//        System.setProperty("org.lwjgl.util.Debug", "true")
    }

    def initWindow(): Unit = {
        glfwErrorCallback = GLFWErrorCallback.createPrint(System.err)

        if (!glfwInit())
            throw new IllegalStateException("Unable to start GLFW")

        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)

        windowId = glfwCreateWindow(WIDTH, HEIGHT, TITLE, MemoryUtil.NULL, MemoryUtil.NULL)
        if(windowId == MemoryUtil.NULL)
            throw new RuntimeException("Failed to create the GLFW window")

        keyCallback = new GLFWKeyCallback() {
            override def invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int): Unit = {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                    glfwSetWindowShouldClose(window, true)
            }
        }

        glfwSetKeyCallback(windowId, keyCallback)

        val vidMode: GLFWVidMode = glfwGetVideoMode(glfwGetPrimaryMonitor())

        glfwSetWindowPos(
            windowId,
            (vidMode.width() - WIDTH) / 2,
            (vidMode.height() - HEIGHT) / 2
        )

        glfwMakeContextCurrent(windowId)

        glfwSwapInterval(1)

        glfwShowWindow(windowId)
    }

    def initGL(): Unit = {
        GL.createCapabilities()

        glClearColor(r, g, b, a)

        vaoId = glGenVertexArrays()
        glBindVertexArray(vaoId)

        vboId = glGenBuffers()

        vboiId = glGenBuffers()

        val vertBuff: FloatBuffer = BufferUtils.createFloatBuffer(verts.length)
        vertBuff.put(verts)
        vertBuff.flip()

        val indsBuff: ByteBuffer = BufferUtils.createByteBuffer(inds.length)
        indsBuff.put(inds)
        indsBuff.flip()

        glBindBuffer(GL_ARRAY_BUFFER, vboId)
        glBufferData(GL_ARRAY_BUFFER, vertBuff, GL_STATIC_DRAW)

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboiId)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indsBuff, GL_STATIC_DRAW)

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
        glBindVertexArray(0)
    }

    def loop(): Unit = {

        while (!glfwWindowShouldClose(windowId)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)

            r = slideRand(r, rd)
            if (r == 0.0f || r == 1.0f)
                rd *= -1
            g = slideRand(g, gd)
            if (g == 0.0f || g == 1.0f)
                gd *= -1
            b = slideRand(b, bd)
            if (b == 0.0f || b == 1.0f)
                bd *= -1
            a = slideRand(a, ad)
            if (a == 0.0f || a == 1.0f)
                ad *= -1
            glClearColor(r, g, b, a)

            glBindVertexArray(vaoId)
            glEnableVertexAttribArray(0)

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboiId)

            glDrawElements(GL_TRIANGLES, inds.length, GL_UNSIGNED_BYTE, 0)

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)

            glDisableVertexAttribArray(0)
            glBindVertexArray(0)

            glfwSwapBuffers(windowId)

            glfwPollEvents()
        }

        glDisableVertexAttribArray(0)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glDeleteBuffers(vboId)

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
        glDeleteBuffers(vboiId)

        glBindVertexArray(0)
        glDeleteVertexArrays(vaoId)

        keyCallback.free()
        glfwErrorCallback.free()
        glfwDestroyWindow(windowId)
    }

    def slideRand(in: Float, d: Int, scale: Float = 20f): Float = Math.max(0.0f, Math.min(in + ((Math.random().toFloat * d) / scale), 1.0f))
}
