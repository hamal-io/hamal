package io.hamal.lib.nodes

import io.hamal.lib.common.hot.HotObjectModule
import io.hamal.lib.common.serialization.HotModule
import io.hamal.lib.common.serialization.JsonFactoryBuilder
import io.hamal.lib.common.serialization.ValueObjectIdAdapter
import io.hamal.lib.common.serialization.ValueObjectStringAdapter
import io.hamal.lib.domain.Json
import io.hamal.lib.domain.vo.ValueObjectJsonModule
import io.hamal.lib.typesystem.TypesystemHotModule


object NodesHotModule : HotModule() {
    init {
        this[NodeId::class] = ValueObjectIdAdapter(::NodeId)
        this[NodeType::class] = ValueObjectStringAdapter(::NodeType)

        this[PortId::class] = ValueObjectIdAdapter(::PortId)
        this[PortName::class] = ValueObjectStringAdapter(::PortName)
        this[ConnectionId::class] = ValueObjectIdAdapter(::ConnectionId)
    }
}


val json = Json(
    JsonFactoryBuilder()
        .register(HotObjectModule)
        .register(ValueObjectJsonModule)
        .register(NodesHotModule)
        .register(TypesystemHotModule)
)