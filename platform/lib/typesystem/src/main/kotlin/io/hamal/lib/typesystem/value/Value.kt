package io.hamal.lib.typesystem.value

import io.hamal.lib.typesystem.Kind

sealed interface Value {
    val kind: Kind
}