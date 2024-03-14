package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjectHotObject
import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.domain.ValueObjectString
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain._enum.NamespaceFeature
import io.hamal.lib.domain._enum.NamespaceFeature.*

class NamespaceId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))

    companion object {
        val root = NamespaceId(1337)
    }
}

class NamespaceName(override val value: String) : ValueObjectString() {
    companion object {
        val default = NamespaceName("default")
    }
}

class NamespaceFeatures(override var value: HotObject = HotObject.empty) : ValueObjectHotObject() {

    init {
        value.nodes.forEach { feature ->
            require(
                NamespaceFeature.entries.any { validFeatures ->
                    validFeatures.name == feature.key && validFeatures.value == feature.value.intValue
                }
            ) { IllegalArgumentException("$feature is not a valid feature.") }
        }
    }

    fun hasFeature(feature: NamespaceFeature): Boolean {
        return value.nodes.containsKey(feature.toString())
    }

    companion object {
        val default = NamespaceFeatures(
            HotObject.builder()
                .set(Schedule.name, Schedule.value)
                .set(Topic.name, Topic.value)
                .set(Webhook.name, Webhook.value)
                .set(Endpoint.name, Endpoint.value)
                .build()
        )
    }
}
