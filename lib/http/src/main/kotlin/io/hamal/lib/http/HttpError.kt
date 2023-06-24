package io.hamal.lib.http

import kotlinx.serialization.Serializable

class HttpError(message: String) : Error(message)