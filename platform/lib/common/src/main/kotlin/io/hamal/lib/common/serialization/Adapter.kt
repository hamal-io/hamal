package io.hamal.lib.common.serialization

import com.google.gson.JsonDeserializer
import com.google.gson.JsonSerializer

interface Adapter<T : Any> : JsonSerializer<T>, JsonDeserializer<T>

interface AdapterGeneric<T : Any> : Adapter<T>

interface AdapterJson<T : Any> : Adapter<T>

interface AdapterHon<T : Any> : Adapter<T>