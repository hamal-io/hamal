package io.hamal.lib.nodes

import io.hamal.lib.common.hot.HotObjectModule
import io.hamal.lib.common.serialization.HotModule
import io.hamal.lib.common.serialization.JsonAdapters
import io.hamal.lib.common.serialization.JsonFactoryBuilder
import io.hamal.lib.common.serialization.ValueObjectIdAdapter
import io.hamal.lib.common.value.TypesystemHotModule
import io.hamal.lib.domain.Json
import io.hamal.lib.domain.vo.ValueObjectJsonModule
import io.hamal.lib.domain.vo.ValueVariableJsonModule
import io.hamal.lib.nodes.control.Control
import io.hamal.lib.nodes.control.ControlExtension
import io.hamal.lib.nodes.control.ControlIdentifier
import io.hamal.lib.nodes.control.ControlType


object NodesHotModule : HotModule() {
    init {
        this[ConnectionId::class] = ValueObjectIdAdapter(::ConnectionId)

        this[ControlIdentifier::class] = JsonAdapters.String(::ControlIdentifier)
        this[ControlType::class] = JsonAdapters.String(::ControlType)
        this[Control::class] = Control.Adapter
        this[ControlExtension::class] = ControlExtension.Adapter

        this[NodeId::class] = ValueObjectIdAdapter(::NodeId)
        this[NodeType::class] = JsonAdapters.String(::NodeType)
        this[NodeTitle::class] = JsonAdapters.String(::NodeTitle)

        this[PortId::class] = ValueObjectIdAdapter(::PortId)
    }
}


val json = Json(
    JsonFactoryBuilder()
        .register(HotObjectModule)
        .register(ValueObjectJsonModule)
        .register(ValueVariableJsonModule)
        .register(NodesHotModule)
        .register(TypesystemHotModule)
)