package io.hamal.lib.common.serialization

import com.google.gson.JsonDeserializer
import com.google.gson.JsonSerializer

interface JsonAdapter<T : Any> : JsonSerializer<T>, JsonDeserializer<T>