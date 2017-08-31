package de.liz3.liz3web.util.extension.js.backend

import com.eclipsesource.v8.V8Function
import com.eclipsesource.v8.V8Object
import com.teamdev.jxbrowser.chromium.Browser
import de.liz3.liz3web.util.extension.Port
import de.liz3.liz3web.util.extension.transportToJsObject

/**
 * Created by liz3 on 30.07.17.
 */
class MessageListener(val port: Port, val browser: Browser) {

    fun addListener(func: V8Function) = port.fromListener.addElement(func)

    fun postMessage(obj: V8Object) {

        val backOb = browser.executeJavaScriptAndReturnValue("{}")
        transportToJsObject(obj, backOb, browser)
        for(method in port.toListener) {
            method.invoke(null, backOb)
        }
    }

}