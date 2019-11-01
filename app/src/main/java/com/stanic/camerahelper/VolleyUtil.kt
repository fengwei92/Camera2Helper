package com.stanic.sjty_pda.util


import android.content.Context
import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.stanic.camerahelper.MyApplication
import com.stanic.csy.MyStringRequest
import com.stanic.pda.util.VolleyTool


object VolleyUtil {
    private const val TAG = "VolleyUtil"
    private var mRequestQueue : RequestQueue? = null

    fun get(url: String, listener: OnResponse<String>) {
        var urlStr = url
        val map = HashMap<String, String>()
        listener.onMap(map)
        val param = prepareParam(map)
        if (param.trim().isNotEmpty()) {
            urlStr += "?$param"
        }
        Log.e(TAG, "urlStr---->$urlStr")
        mRequestQueue = VolleyTool.getInstance(MyApplication.getContext()).requestQueue
        val stringRequest = MyStringRequest(urlStr, Response.Listener { response ->
            listener.onSuccess(response)
        }, Response.ErrorListener {
            listener.onError(it)
        })
        mRequestQueue?.add(stringRequest)
    }

//    fun getWithProgressDialog(context: Context, url: String, content: String,listener: OnResponse<String>) {
//
//        var urlStr = url
//        val map = HashMap<String, String>()
//        listener.onMap(map)
//        val param = prepareParam(map)
//        if (param.trim().isNotEmpty()) {
//            urlStr += "?$param"
//        }
//        Log.e(TAG, "urlStr---->$urlStr")
//        mRequestQueue = VolleyTool.getInstance(context).requestQueue
//        val stringRequest = MyStringRequest(urlStr, Response.Listener { response ->
//            if (isJsonValid(response)){
//           listener.onSuccess(response)
//            }else{
//                //设置网络界面需要
//                listener.onSuccess(response)
//            }
//
//        }, Response.ErrorListener {
//            listener.onError(it)
//
//        })
//        mRequestQueue?.add(stringRequest)
//    }

    private fun prepareParam(paramMap: Map<String, String>): String {
        val sb = StringBuilder()
        return if (paramMap.isEmpty()) {
            ""
        } else {
            for (key in paramMap.keys) {
                val value = paramMap[key]
                if (sb.isEmpty()) {
                    sb.append(key).append("=").append(value)
                } else {
                    sb.append("&").append(key).append("=").append(value)
                }
            }
            sb.toString()
        }
    }


    interface OnResponse<T> {
        fun onMap(map: HashMap<String, String>)

        fun onSuccess(response: T)

        fun onError(error: VolleyError)
    }
}