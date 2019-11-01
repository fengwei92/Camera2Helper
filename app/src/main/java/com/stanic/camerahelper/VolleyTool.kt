package com.stanic.pda.util

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley


class VolleyTool private constructor(private val mContext: Context) {
    private var mRequestQueue: RequestQueue? = null

    val requestQueue: RequestQueue
        get() {
            if (mRequestQueue == null) {
                mRequestQueue = Volley.newRequestQueue(mContext)
            }
            return mRequestQueue!!
        }

    init {
        mRequestQueue = Volley.newRequestQueue(mContext)
    }

    companion object {
        private var mInstance: VolleyTool? = null

        @Synchronized
        fun getInstance(context: Context): VolleyTool {
            if (mInstance == null) {
                mInstance = VolleyTool(context)
            }
            return mInstance!!
        }
    }

}
