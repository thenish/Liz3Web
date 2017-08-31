package de.liz3.liz3web.util.extension.js.backend

import com.eclipsesource.v8.V8Function
import de.liz3.liz3web.util.extension.ExtensionExecutor

/**
 * Created by liz3 on 28.07.17.
 */
class ConnectListener(val executor: ExtensionExecutor) {

    fun addListener(callback:V8Function) {

        executor.portListener.addElement(callback)
    }
}