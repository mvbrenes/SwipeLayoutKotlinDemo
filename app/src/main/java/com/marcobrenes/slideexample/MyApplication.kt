package com.marcobrenes.slideexample

import android.app.Application
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

/**
 * Created by marco on 11/19/2017.
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        requestQueue = Volley.newRequestQueue(applicationContext)
    }

    fun <T> addToRequestQueue(req: Request<T>, tag: String = TAG) {
        // set the default tag if the tag is empty
        req.tag = tag
        requestQueue.add(req)
    }

    fun cancelPendingRequest(tag: Any) {
        requestQueue.cancelAll(tag)
    }

    companion object {
        val TAG = MyApplication::class.java.simpleName
        private lateinit var requestQueue : RequestQueue
        lateinit var instance: MyApplication
            private set
    }
}
