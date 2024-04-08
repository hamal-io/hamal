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
        this[ConnectionId::class] = ValueObjectIdAdapter(::ConnectionId)

        this[ControlId::class] = ValueObjectIdAdapter(::ControlId)
        this[Control::class] = Control.Adapter

        this[NodeId::class] = ValueObjectIdAdapter(::NodeId)
        this[NodeType::class] = ValueObjectStringAdapter(::NodeType)
        this[NodeTitle::class] = ValueObjectStringAdapter(::NodeTitle)

        this[PortId::class] = ValueObjectIdAdapter(::PortId)
    }
}


val json = Json(
    JsonFactoryBuilder()
        .register(HotObjectModule)
        .register(ValueObjectJsonModule)
        .register(NodesHotModule)
        .register(TypesystemHotModule)
)