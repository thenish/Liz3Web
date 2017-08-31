package de.liz3.liz3web.util.extension

import com.eclipsesource.v8.V8
import com.eclipsesource.v8.V8Array
import com.eclipsesource.v8.V8Object
import com.eclipsesource.v8.V8Value
import com.teamdev.jxbrowser.chromium.Browser
import com.teamdev.jxbrowser.chromium.BrowserType
import com.teamdev.jxbrowser.chromium.JSObject
import com.teamdev.jxbrowser.chromium.JSValue
import java.util.*

/**
 * Created by liz3 on 28.07.17.
 */
 fun transportToV8Object(obj: JSValue, target: V8Object, runtime: V8) {

    if(obj.isObject) {

        for(key in obj.asObject().propertyNames) {
            val value = obj.asObject().getProperty(key)
            if(value.isObject) {
                val newObject = V8Object(runtime)
                transportToV8Object(value, newObject, runtime)
                target.add(key, newObject)
                continue
            }
            if(value.isArray) {
                val newArray = V8Array(runtime)
                transportToV8Object(value, newArray, runtime)
                target.add(key, newArray)
                continue
            }
            if(value.isNumber) {
                target.add(key, value.asNumber().value)
                continue
            }
            if(value.isBoolean) {
                target.add(key, value.asBoolean().value)
                continue
            }
            if(value.isBoolean) {
                target.add(key, value.asBoolean().value)
                continue
            }
            if(value.isString) {
                target.add(key, value.asString().value)
                continue
            }
        }

        return
    }
    if(obj.isArray) {

        val realTarget = target as V8Array
        for(index in 0..obj.asArray().length()) {
            val value = obj.asArray()[index]

            if(value.isObject) {
                val newObject = V8Object(runtime)
                transportToV8Object(value, newObject, runtime)
                realTarget.push(newObject)
                continue
            }
            if(value.isArray) {
                val newArray = V8Array(runtime)
                transportToV8Object(value, newArray, runtime)
                target.push(newArray)
                continue
            }
            if(value.isNumber) {
                target.push( value.asNumber().value)
                continue
            }
            if(value.isBoolean) {
                target.push( value.asBoolean().value)
                continue
            }
            if(value.isBoolean) {
                target.push( value.asBoolean().value)
                continue
            }
            if(value.isString) {
                target.push( value.asString().value)
                continue
            }
        }

    }

}
fun transportToJsObject(source:V8Value, target: JSValue, browser:Browser = Browser(BrowserType.LIGHTWEIGHT)) {


    if(source is V8Object) {

        for(key in source.keys) {
            val value = source.get(key)
            if(value is V8Object) {
                val newObj = browser.executeJavaScriptAndReturnValue("{}")
                transportToJsObject(value, newObj.asObject(), browser)
                target.asObject().setProperty(key, newObj)
                continue
            }
            if(value is V8Array) {

                val newArr = browser.executeJavaScriptAndReturnValue("[]")
                transportToJsObject(value, newArr, browser)
                target.asObject().setProperty(key, newArr)
                continue
            }

            target.asObject().setProperty(key, value)
        }

    }
    if(source is V8Array) {

        for(index in 0..source.length()){
            val value = source.get(index)

            if(value is V8Object) {
                val newObj = browser.executeJavaScriptAndReturnValue("{}")
                transportToJsObject(value, newObj.asObject(), browser)
                target.asArray().set(target.asArray().length() + 1, newObj)

                continue
            }
            if(value is V8Array) {
                val newObj = browser.executeJavaScriptAndReturnValue("[]")
                transportToJsObject(value, newObj.asArray(), browser)
                target.asArray().set(target.asArray().length() + 1, newObj)
                continue
            }

            target.asArray().set(target.asArray().length() + 1, value)

        }
    }

}
class ListenerList : Vector<Any>() {

    private val addListener = Vector<Runnable>()
    private val remListener = Vector<Runnable>()
    var last: Any? = null

    override fun add(element: Any?): Boolean {
        last = element
        for(listener in addListener) {
            listener.run()
        }
        return super.add(element)

    }

    override fun remove(element: Any?): Boolean {
        last = element
        for(listener in remListener) {
            listener.run()
        }
        return super.remove(element)
    }

    fun addPushListener(run:Runnable) {
        addListener.addElement(run)
    }
    fun addRemoveListener(run:Runnable) {
        remListener.addElement(run)
    }
}
