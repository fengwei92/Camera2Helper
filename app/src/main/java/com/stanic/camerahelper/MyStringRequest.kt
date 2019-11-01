package com.stanic.csy

import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.ParseError
import org.json.JSONException
import com.android.volley.toolbox.HttpHeaderParser
import java.io.UnsupportedEncodingException


class MyStringRequest( url: String?, listener: Response.Listener<String>?, errorListener: Response.ErrorListener?) : StringRequest(url, listener, errorListener){


    override fun parseNetworkResponse(response: NetworkResponse?): Response<String>? {
        return try {
            val jsonString = String(response!!.data,  Charsets.UTF_8)
            Response.success<String>(jsonString,
                    HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: UnsupportedEncodingException) {
            Response.error<String>(ParseError(e))
        } catch (je: JSONException) {
            Response.error<String>(ParseError(je))
        }

    }


}