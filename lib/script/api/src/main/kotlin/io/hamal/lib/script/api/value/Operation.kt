package io.hamal.lib.script.api.value

sealed interface ValueOperation {
    val operationType: Type

    enum class Type(val identifier: String) {
        Add("__add"),       // self + other
        Div("__div"),       // self / other
        Eq("__eq"),         // self == other
        Gt("__gt"),         // self > other
        Gte("__gte"),       // self >= other
        Lt("__lt"),         // self < other
        Lte("__lte"),       // self <= other
        Neq("__neq"),       // self ~= other
        Mod("__mod"),       // self % other
        Mul("__mul"),       // self * other
        Negate("__unm"),    // - self
        Pow("__pow"),       // self ^ other
        Sub("__sub"),       // self - other
    }
}

interface PrefixValueOperation : ValueOperation {
    val selfType: String
    operator fun invoke(self: Value): Value
}

interface InfixValueOperation : ValueOperation {
    val selfType: String
    val otherType: String
    operator fun invoke(self: Value, other: Value): Value
}
