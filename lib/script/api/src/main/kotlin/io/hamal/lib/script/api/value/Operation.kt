package io.hamal.lib.script.api.value

sealed interface ValueOperation {
    val operationType: Type

    enum class Type(identifier: String) {
        Add("__add"),       // self + other
        Div("__div"),       // self / other
        GT("__gt"),         // self > other
        GTE("__gte"),       // self >= other
        LT("__lt"),         // self < other
        LTE("__lte"),       // self <= other
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
