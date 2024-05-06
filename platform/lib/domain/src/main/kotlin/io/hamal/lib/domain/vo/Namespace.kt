package io.hamal.lib.domain.vo

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.*
import io.hamal.lib.domain._enum.NamespaceFeatures
import io.hamal.lib.domain._enum.NamespaceFeatures.*
import io.hamal.lib.domain.vo.NamespaceFeature.Companion.NamespaceFeature

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

class NamespaceFeature(override val value: ValueEnum) : ValueVariableEnum<NamespaceFeatures>(NamespaceFeatures::class) {
    companion object {
        fun NamespaceFeature(value: Enum<NamespaceFeatures>) = NamespaceFeature(ValueEnum(value.name))
    }
}

class NamespaceFeaturesMap(override var value: ValueObject = ValueObject.empty) : ValueVariableObject() {

    init {
        value.values.forEach { (feat, value) ->
            require(
                NamespaceFeatures.entries.any { validFeatures ->
                    validFeatures.name == feat
                }
            ) { IllegalArgumentException("$feat is not a valid feature.") }
        }
    }

    fun hasFeature(feature: NamespaceFeature): Boolean {
        return value.values.map { it.key }.contains(feature.stringValue)
    }

    companion object {
        val default = NamespaceFeaturesMap(
            ValueObject.builder()
                .set(NamespaceFeature(schedule).stringValue, true)
                .set(NamespaceFeature(topic).stringValue, false)
                .set(NamespaceFeature(webhook).stringValue, false)
                .set(NamespaceFeature(endpoint).stringValue, false)
                .build()
        )
    }
}



