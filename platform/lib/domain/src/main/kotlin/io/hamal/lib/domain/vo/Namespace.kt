package io.hamal.lib.domain.vo

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.*
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


class NamespaceFeatures(override var value: ValueObject = ValueObject.empty) : ValueVariableObject() {

    init {
        value.properties.forEach { prop ->
            require(
                NamespaceFeature.entries.any { validFeatures ->
                    validFeatures.name == prop.identifier.stringValue
                }
            ) { IllegalArgumentException("${prop.identifier} is not a valid feature.") }
        }
    }

    fun hasFeature(feature: NamespaceFeature): Boolean {
        return value.properties.map { it.identifier.stringValue }.contains(feature.name)
    }

    companion object {
        val default = NamespaceFeatures(
            ValueObject.builder()
                .set(schedule.name, true)
                .set(topic.name, false)
                .set(webhook.name, false)
                .set(endpoint.name, false)
                .build()
        )
    }
}

