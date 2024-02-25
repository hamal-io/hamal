package io.hamal.lib.kua.type

import io.hamal.lib.common.hot.*
import io.hamal.lib.kua.State
import io.hamal.lib.kua.array
import io.hamal.lib.kua.map

//FIXME replace toKua with this
fun HotNode.toKua(state: State): KuaType {
    return when (this) {
        is HotObject -> state.map(nodes.map { (key, value) -> key to value.toKua(state) }.toMap())
        is HotArray -> state.array(nodes.map { it.toKua(state) })
        is HotBoolean -> if (value) KuaTrue else KuaFalse
        is HotNull -> KuaNil
        is HotNumber -> KuaNumber(value.toDouble())
        is HotString -> KuaString(value)
        else -> TODO()
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
        is KuaTable.Map -> toHotObject()
        is KuaTable.Array -> toHotArray()
        is KuaTable -> toHotNode()
        is KuaTableType -> TODO()
    }
}

fun KuaTable.Map.toHotObject(): HotObject {
    val builder = HotObject.builder()
//    this.underlyingMap.forEach { (key, value) ->
//        builder[key] = value.toHot()
//    }

    entries().forEach { (key, value) ->
        builder[key.value] = value.toHot()
    }


    return builder.build()
}


fun KuaTable.Array.toHotArray(): HotArray {
    val builder = HotObject.builder()
//    this.value.forEach { (key, value) ->
//        builder[key] = value.toHot()
//    }
//    return builder.build()
    TODO()
}

fun KuaTable.toHotNode(): HotObject {
//    val builder = HotObject.builder()
//    this.underlyingMap.forEach { (key, value) ->
//        builder[key] = value.toHot()
//    }
//    return builder.build()
    TODO()
}
