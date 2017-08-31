package de.liz3.liz3web.util.extension

import com.eclipsesource.v8.V8Function
import com.teamdev.jxbrowser.chromium.JSFunction
import com.teamdev.jxbrowser.chromium.JSObject
import java.util.*


/**
 * Created by liz3 on 03.07.17.
 */

class Port {
    val toListener = Vector<JSFunction>()
    val fromListener = Vector<V8Function>()

}