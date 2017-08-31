package de.liz3.liz3web.util.extension

import com.eclipsesource.v8.V8
import com.eclipsesource.v8.V8Array
import com.eclipsesource.v8.V8Function
import com.eclipsesource.v8.V8Object
import com.eclipsesource.v8.utils.ConcurrentV8
import com.teamdev.jxbrowser.chromium.Browser
import com.teamdev.jxbrowser.chromium.JSFunction
import com.teamdev.jxbrowser.chromium.events.ScriptContextAdapter
import com.teamdev.jxbrowser.chromium.events.ScriptContextEvent
import com.teamdev.jxbrowser.chromium.javafx.BrowserView
import de.liz3.liz3web.util.extension.js.backend.Chrome
import de.liz3.liz3web.util.extension.js.backend.ChromeRuntime
import de.liz3.liz3web.util.extension.js.backend.ConnectListener
import de.liz3.liz3web.util.extension.js.backend.MessageListener
import de.liz3.liz3web.util.extension.js.frontend.Runtime
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import org.binaryone.jutils.io.FileUtils
import org.json.JSONObject
import java.io.File
import java.util.*
import javafx.scene.Scene
import javafx.stage.Modality


/**
 * Created by liz3 on 03.07.17.
 */
class ExtensionManager(extensionFolder: File) {

    val extensions = Vector<ExtensionExecutor>()

    init {

        if (extensionFolder.isDirectory) {

            for (f in extensionFolder.listFiles()) {
                addExtension(f)
            }
        }


    }

    fun addExtension(folder: File) {

        if (!folder.isDirectory) return

        val config = File(folder, "manifest.json")

        if (!config.exists()) return

        val confObj = JSONObject(FileUtils.readFile(config))

        val name = confObj.getString("name")
        val description = confObj.getString("description")
        val version = confObj.getString("version").toDouble()
        val popup = {
            if (!confObj.has("browser_action")) {
                Pair(false, ExtensionPopUp("", "", ""))
            } else {
                val popObj = confObj.getJSONObject("browser_action")
                val htmlFile = File(folder, popObj.getString("default_popup")).absolutePath
                val eName = popObj.getString("default_title")
                val iconFile = File(folder, popObj.getString("default_icon")).absolutePath

                Pair(true, ExtensionPopUp(htmlFile, eName, iconFile))
            }
        }.invoke()

        val backgroundScripts = {

            val list = Vector<String>()

            if (confObj.has("background")) {
                val scripts = confObj.getJSONObject("background").getJSONArray("scripts")

                scripts.forEach { list.addElement(File(folder, it as String).absolutePath) }
            }

            list.toList()

        }.invoke()

        val contentScripts = {

            val list = Vector<String>()

            if (confObj.has("content_scripts")) {
                val scripts = confObj.getJSONArray("content_scripts").getJSONObject(0).getJSONArray("js")

                scripts.forEach { list.addElement(File(folder, it as String).absolutePath) }
            }

            list.toList()

        }.invoke()

        val extension = {
            if (popup.first) {
                Extension(name, description, version, backgroundScripts, contentScripts, popup.second)
            } else {
                Extension(name, description, version, backgroundScripts, contentScripts, null)

            }
        }.invoke()
        val executor = ExtensionExecutor(extension)
        extensions.addElement(executor)
        ExtensionPipe.list.addElement(executor)

        executor.exec()

    }
}

class ExtensionExecutor(val extension: Extension) {


    var runtimeEngine = ConcurrentV8()
    var runtime = runtimeEngine.v8
    var visible = false


    val portListener = Vector<V8Function>()
    val ports = Vector<Port>()


    fun exec() {


        val thread = Thread(Runnable {
            runtimeEngine.run({

                val ch = Chrome(this)
                val rm = ChromeRuntime(this)
                val portConnectListener = ConnectListener(this)
                val chromeObject = V8Object(runtime)
                val runtimeObject = V8Object(runtime)
                val portConnectListenerObject = V8Object(runtime)
                portConnectListenerObject.registerJavaMethod(portConnectListener, "addListener", "addListener", arrayOf<Class<*>>(V8Function::class.java))
                runtimeObject.add("onConnect", portConnectListenerObject)
                chromeObject.add("runtime", runtimeObject)
                runtime.registerJavaMethod({ reciver, parameter ->
                    println(parameter.get(0))
                }, "println")
                runtime.add("chrome", chromeObject)

                for (x in extension.backgroundScripts) {
                    try {

                        runtime.executeScript(FileUtils.readFile(File(x)))
                        //   extensionBrowser.executeJavaScript(FileUtils.readFile(File(x)))

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }


            })
        })
        thread.name = "Extension Executor Thread for ${extension.name}"
        thread.start()
    }


    fun callPortListener(obj:V8Object, port: Port, browser: Browser) {

        val obje = V8Object(runtime)
        val listerClass = MessageListener(port, browser)
        obje.registerJavaMethod(listerClass, "addListener", "addListener", arrayOf<Class<*>>(V8Function::class.java))
        obj.add("onMessage", obje)
        obj.registerJavaMethod(listerClass, "postMessage", "postMessage", arrayOf<Class<*>>(V8Object::class.java))

        for(caller in portListener) {
            val array = V8Array(runtime)
            array.push(obj)
            caller.call(null, array)
        }
    }

    fun getPopUp(owner: Stage, callback: (Browser) -> Unit): Stage {

        val pane = BorderPane()
        val browser = Browser()
        val view = BrowserView(browser)
        pane.center = view


        val dialog = Stage()
        dialog.scene = Scene(pane, 200.0, 500.0)
        dialog.initModality(Modality.APPLICATION_MODAL)
        dialog.initOwner(owner)
        //  manageNewBrowser(browser)

        browser.addScriptContextListener(object : ScriptContextAdapter() {
            override fun onScriptContextCreated(event: ScriptContextEvent?) {
                val window = browser.executeJavaScriptAndReturnValue("chrome")
                window.asObject().setProperty("runtime", Runtime(this@ExtensionExecutor, browser))
            }
        })
        callback.invoke(browser)
        return dialog
    }
}

class Extension(val name: String,
                val description: String,
                val version: Double,
                val backgroundScripts: List<String>, val contentScripts: List<String>, val popUp: ExtensionPopUp? = null)

class ExtensionPopUp(val htmlFile: String, val title: String, val icon: String)

object ExtensionPipe {
    val list = Vector<ExtensionExecutor>()
}

