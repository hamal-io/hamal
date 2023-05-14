package io.hamal.lib.script.api.native_

import io.hamal.lib.script.api.ast.Identifier

data class NativeIdentifier(
    override val value: String
) : Identifier