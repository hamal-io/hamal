package io.hamal.lib.kua.type


@JvmInline
value class KuaString(private val value: String) : KuaType {
    override fun toString(): String = value
    val stringValue: String get() = value
}
