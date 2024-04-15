package io.hamal.lib.typesystem.type

data object TypeNil : Type() {
    override val identifier = TypeIdentifier("nil")
}
