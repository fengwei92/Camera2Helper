package com.stanic.camerahelper

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context


/**
 * @author fengw
 */
class MyApplication : Application() {
    private var mInstance : MyApplication? = null

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var mContext : Context? = null
        public fun getContext() : Context{
            return mContext!!
        }

    }

    override fun onCreate() {
        super.onCreate()
        mContext = applicationContext
    }

   public fun getInstance() : MyApplication{
        if (mInstance == null){
            mInstance = MyApplication()

        }
            return mInstance!!

   }



}
