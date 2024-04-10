package io.hamal.lib.typesystem.value

import io.hamal.lib.typesystem.Field.Kind

sealed interface Value {
    val kind: Kind
}