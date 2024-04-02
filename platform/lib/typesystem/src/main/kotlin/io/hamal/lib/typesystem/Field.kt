package io.hamal.lib.typesystem

import java.util.*

enum class Kind {
    Boolean,
    Date,
    Decimal,
    List,
    Nil,
    Number,
    Object,
    String,
    Time,
}

data class Field(
    val kind: Kind,
    val identifier: String,
    val name: String = identifier.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
    val isList: Boolean = false,
    val objectKind: Type? = null,
) {
    init {
        if ((objectKind == null) xor (kind != Kind.Object)) {
            TODO()
        }
    }
}
