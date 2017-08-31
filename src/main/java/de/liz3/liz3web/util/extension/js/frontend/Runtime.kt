package de.liz3.liz3web.util.extension.js.frontend

import com.eclipsesource.v8.V8Array
import com.eclipsesource.v8.V8Object
import com.teamdev.jxbrowser.chromium.Browser
import com.teamdev.jxbrowser.chromium.JSObject
import de.liz3.liz3web.util.extension.ExtensionExecutor
import de.liz3.liz3web.util.extension.Port
import de.liz3.liz3web.util.extension.transportToV8Object


/**
 * Created by liz3 on 28.07.17.
 */
class Runtime(val executor: ExtensionExecutor, val browser: Browser) {

    fun connect(obj: JSObject): JSObject {

        val backVal = obj

       try {
           println(obj)
           val port = Port()
           backVal.asObject().setProperty("onMessage", MessageListener(browser, port))
           backVal.asObject().setProperty("postMessage" , { obj:JSObject ->
               executor.runtimeEngine.run {
                   val nObj = V8Object(executor.runtime)
                   transportToV8Object(obj, nObj, executor.runtime)
                   for(method in port.fromListener) {
                       val arr = V8Array(executor.runtime)
                       arr.push(nObj)
                       method.call(null, arr)
                   }
               }
           })

           executor.runtimeEngine.run { v8 ->
               executor.ports.add(port)

               val v8Obj = V8Object(executor.runtime)
               transportToV8Object(obj, v8Obj, executor.runtime)
               println(v8Obj.get("name"))
               executor.callPortListener(v8Obj, port, browser)
           }



       }catch (e:Exception) {
           e.printStackTrace()
       }
        return backVal
    }

}