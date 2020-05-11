package com.nmg.baseinfrastructure.data.remote

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext


open class ContextProviders {
    open val Main: CoroutineContext = Dispatchers.Main
    open val IO: CoroutineContext = Dispatchers.IO

    companion object {

      open  fun getInstance() : ContextProviders {
            return  synchronized(this) {
                ContextProviders()
            }
        }
    }
}