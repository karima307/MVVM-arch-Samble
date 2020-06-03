package com.nmg.baseinfrastructure.data.remote

import android.annotation.SuppressLint
import android.util.Log
import com.nmg.baseinfrastructure.App
import com.nmg.baseinfrastructure.R
import com.google.gson.stream.MalformedJsonException

import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Created by Your name on 1/28/2020.
 */
interface ErrorHandler {
    fun getErrorFromBody(errorBody: String?): String?


    @SuppressLint("LogNotTimber")
    fun getHttpExceptionError(error: Throwable): String?
}