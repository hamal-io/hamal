package io.hamal.lib.meta

interface Tuple {
    val size: Int
}

class Tuple0 : Tuple {
    override val size: Int = 0
    override fun toString() = "()"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return javaClass == other?.javaClass
    }

    override fun hashCode() = 2810
}

data class Tuple1<TYPE_1>(
    val _1: TYPE_1
) : Tuple {
    override val size: Int = 1
    override fun toString() = "($_1)"
}

data class Tuple2<TYPE_1, TYPE_2>(
    val _1: TYPE_1,
    val _2: TYPE_2,
) : Tuple {
    override val size: Int = 2
    override fun toString() = "($_1,$_2)"
}