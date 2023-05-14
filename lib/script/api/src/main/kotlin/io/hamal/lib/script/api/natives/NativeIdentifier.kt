package io.hamal.lib.script.api.natives

import io.hamal.lib.script.api.ast.Identifier

data class NativeIdentifier(
    override val value: String
) : Identifier