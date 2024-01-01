package io.hamal.lib.common.serialization

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.hamal.lib.common.hot.HotArray
import io.hamal.lib.common.hot.HotObject
import java.time.Instant

object GsonFactory {

    private val builder = GsonBuilder()

    init {
        registerTypeAdapter(HotObject::class.java, HotObjectAdapter)
        registerTypeAdapter(HotArray::class.java, HotArrayAdapter)
        registerTypeAdapter(Instant::class.java, InstantAdapter)
    }

    fun <TYPE, SERDE : GsonSerde<TYPE>> registerTypeAdapter(clazz: Class<TYPE>, serde: SERDE): GsonFactory {
        builder.registerTypeAdapter(clazz, serde)
        return this
    }

    fun create(): Gson = builder.create()
}