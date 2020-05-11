package com.nmg.baseinfrastructure.utils

import android.util.Log

object AdLog {

    fun logError(t: String, m: String, e: Throwable) {
        Log.e(t, m, e)
    }

    fun logInfo(t: String, m: String) {
        Log.i(t, m)
    }

    fun logDebug(t: String, m: String) {
        Log.d(t, m)
    }

    fun logCrashlytics(t: String, m: String, e: Throwable) {
        Log.e(t, m, e)
        //Crashlytics.logException(e)
    }
}