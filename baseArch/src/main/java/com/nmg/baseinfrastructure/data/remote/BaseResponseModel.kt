package com.nmg.baseinfrastructure.data.remote



abstract class BaseResponseModel{

   abstract fun getSuccess() : Any?
   abstract fun getError() : String?
}