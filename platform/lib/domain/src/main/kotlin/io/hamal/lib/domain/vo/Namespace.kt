package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.DomainId
import io.hamal.lib.common.domain.DomainIdSerializer
import io.hamal.lib.common.domain.DomainName
import io.hamal.lib.common.domain.DomainNameSerializer
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.base.Inputs
import io.hamal.lib.domain.vo.base.InputsSerializer
import io.hamal.lib.kua.type.MapType
import kotlinx.serialization.Serializable

@Serializable(with = NamespaceId.Serializer::class)
class NamespaceId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))

    internal object Serializer : DomainIdSerializer<NamespaceId>(::NamespaceId)

    companion object {
        val root = NamespaceId(1)
    }
}


//FIXME make sure namespace name does not contain any whitespaces and does not end with ::
@Serializable(with = NamespaceName.Serializer::class)
class NamespaceName(override val value: String) : DomainName() {
    internal object Serializer : DomainNameSerializer<NamespaceName>(::NamespaceName)

    fun allNamespaceNames(): List<NamespaceName> {
        val parts = value.split("::")
        val result = mutableListOf<NamespaceName>()
        for (x in IntRange(0, parts.size - 1)) {
            val builder = StringBuilder()
            for (y in IntRange(0, x)) {
                builder.append(parts[y])
                builder.append("::")
            }
            result.add(NamespaceName(builder.toString().let { it.substring(0, it.length - 2) }))
        }
        return result
    }
}

@Serializable(with = NamespaceInputs.Serializer::class)
class NamespaceInputs(override val value: MapType = MapType()) : Inputs() {
    internal object Serializer : InputsSerializer<NamespaceInputs>(::NamespaceInputs)
}
