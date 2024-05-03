package io.hamal.lib.common.serialization

import com.google.gson.JsonDeserializer
import com.google.gson.JsonSerializer


interface AdapterJson<T : Any> : JsonSerializer<T>, JsonDeserializer<T>

interface AdapterHon<T : Any> : JsonSerializer<T>, JsonDeserializer<T>