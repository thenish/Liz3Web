package de.liz3.liz3web.util.extension.js.frontend

import com.teamdev.jxbrowser.chromium.Browser
import com.teamdev.jxbrowser.chromium.JSFunction
import de.liz3.liz3web.util.extension.Port

/**
 * Created by liz3 on 30.07.17.
 */
class MessageListener(val browser: Browser, val port:Port) {

    fun addListener(func:JSFunction) = port.toListener.addElement(func)


}