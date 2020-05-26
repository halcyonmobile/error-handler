package com.halcyonmobile.errorhandling.error

import com.halcyonmobile.errorhandlerrest.exception.RemoteException
import com.halcyonmobile.errorhandling.util.ApiErrorModel

data class DemoAppApiError(val payload: ApiErrorModel) : RemoteException()