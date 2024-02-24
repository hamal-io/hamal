package io.hamal.lib.kua.type

import io.hamal.lib.common.hot.*

fun HotNode.toKua(): KuaType {
    return when (this) {
        is HotObject -> KuaTable(
            nodes.map { (key, node) ->
                key to node.toKua()
            }.toMap().toMutableMap()
        )

//        is HotArray -> KuaArray(
//            nodes.mapIndexed { index, node ->
//                index to node.toKua()
//            }.toMap().toMutableMap()
//        )
        is HotArray -> TODO()

        is HotBoolean -> if (value) KuaTrue else KuaFalse
        is HotNull -> KuaNil
        is HotNumber -> KuaNumber(value.toDouble())
        is HotString -> KuaString(value)
    }
}

fun KuaType.toHot(): HotNode {
    return when (this) {
        is KuaAny -> value.toHot()
        is KuaFalse -> HotBoolean(false)
        is KuaTrue -> HotBoolean(true)
        is KuaCode -> HotString(value)
        is KuaDecimal -> TODO()
        is KuaError -> TODO()
        is KuaFunction<*, *, *, *> -> TODO()
        is KuaNil -> HotNull
        is KuaNumber -> HotNumber(value)
        is KuaString -> HotString(value)
        is KuaTableType -> toHotObject()
    }
}

fun KuaType.toHotObject(): HotObject {
    require(this is KuaTable)
    val builder = HotObject.builder()
    this.value.forEach { (key, value) ->
        builder[key] = value.toHot()
    }
    return builder.build()
}