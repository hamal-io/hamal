package io.hamal.lib.kua.type


@JvmInline
value class KuaCode(private val value: String) : KuaType {
    val stringValue: String get() = value
}