package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjectHotObject
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.ValueSnowflakeId
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.common.value.ValueVariableSnowflakeId
import io.hamal.lib.common.value.ValueVariableString
import io.hamal.lib.domain._enum.NamespaceFeature
import io.hamal.lib.domain._enum.NamespaceFeature.*

class NamespaceId(override val value: ValueSnowflakeId) : ValueVariableSnowflakeId() {
    companion object {
        val root = NamespaceId(1337)

        fun NamespaceId(value: SnowflakeId) = NamespaceId(ValueSnowflakeId(value))
        fun NamespaceId(value: Int) = NamespaceId(ValueSnowflakeId(SnowflakeId(value.toLong())))
        fun NamespaceId(value: String) = NamespaceId(ValueSnowflakeId(SnowflakeId(value.toLong(16))))
    }
}

class NamespaceName(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun NamespaceName(value: String) = NamespaceName(ValueString(value))
        val default = NamespaceName("default")
    }
}


class NamespaceFeatures(override var value: HotObject = HotObject.empty) : ValueObjectHotObject() {

    init {
        value.nodes.forEach { feature ->
            require(
                NamespaceFeature.entries.any { validFeatures ->
                    validFeatures.name == feature.key
                }
            ) { IllegalArgumentException("$feature is not a valid feature.") }
        }
    }

    fun hasFeature(feature: NamespaceFeature): Boolean {
        return value.nodes.containsKey(feature.name)
    }

    companion object {
        val default = NamespaceFeatures(
            HotObject.builder()
                .set(schedule.name, true)
                .set(topic.name, false)
                .set(webhook.name, false)
                .set(endpoint.name, false)
                .build()
        )
    }
}

