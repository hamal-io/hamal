package io.hamal.lib.domain.vo

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.*
import io.hamal.lib.domain._enum.Features
import io.hamal.lib.domain._enum.Features.*

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

class NamespaceFeature(override val value: ValueEnum) : ValueVariableEnum<Features>(Features::class) {
    companion object {
        fun NamespaceFeature(value: Enum<Features>) = NamespaceFeature(ValueEnum(value.name))
    }
}

class NamespaceFeatures(override var value: ValueObject = ValueObject.empty) : ValueVariableObject() {

    init {
        value.values.forEach { (feature, value) ->
            require(
                Features.entries.any { validFeatures -> validFeatures.name.lowercase() == feature }
            ) { IllegalArgumentException("$feature is not a valid feature") }
        }
    }

    fun isActive(feature: Features): Boolean {
        return value.findBoolean(feature.name.lowercase()) == ValueTrue
    }

    companion object {
        val default = NamespaceFeatures(
            ValueObject.builder()
                .set(Schedule.name.lowercase(), true)
                .set(Topic.name.lowercase(), false)
                .set(Webhook.name.lowercase(), false)
                .set(Endpoint.name.lowercase(), false)
                .build()
        )
    }
}



