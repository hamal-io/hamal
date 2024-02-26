package io.hamal.lib.kua.type

import io.hamal.lib.common.hot.*
import io.hamal.lib.kua.State
import io.hamal.lib.kua.createTable

//FIXME replace toKua with this
fun HotNode.toKua(state: State): KuaType {
    return when (this) {
        is HotObject -> state.createTable(nodes.map { (key, value) -> key to value.toKua(state) }.toMap())
        is HotArray -> state.createTable(nodes.map { it.toKua(state) })
        is HotBoolean -> if (value) KuaTrue else KuaFalse
        is HotNull -> KuaNil
        is HotNumber -> KuaNumber(value.toDouble())
        is HotString -> KuaString(value)
        else -> TODO()
    }
}


fun KuaType.toHotNode(): HotNode {
    return when (this) {
        is KuaAny -> value.toHotNode()
        is KuaFalse -> HotBoolean(false)
        is KuaTrue -> HotBoolean(true)
        is KuaCode -> HotString(value)
        is KuaDecimal -> TODO()
        is KuaError -> TODO()
        is KuaFunction<*, *, *, *> -> TODO()
        is KuaNil -> HotNull
        is KuaNumber -> HotNumber(value)
        is KuaString -> HotString(value)
        is KuaTable -> toHotObject()
    }
}

fun KuaTable.toHotObject(): HotObject {
    val builder = HotObject.builder()
//    this.underlyingMap.forEach { (key, value) ->
//        builder[key] = value.toHot()
//    }

    mapEntries().forEach { (key, value) ->
        builder[key.value] = value.toHotNode()
    }

//    entries().forEach { (key, value) ->
//        builder[key.value] = value.toHot()
//    }
//

    return builder.build()
}