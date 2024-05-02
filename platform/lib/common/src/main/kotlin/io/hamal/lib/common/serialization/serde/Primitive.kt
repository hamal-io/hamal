package io.hamal.lib.common.serialization.serde

sealed interface SerdePrimitive<NODE_TYPE : SerdePrimitive<NODE_TYPE>> : SerdeNode<NODE_TYPE> {
    override val isPrimitive get() : Boolean = true
    override fun asPrimitive(): SerdePrimitive<*> = this
}