package io.hamal.lib.nodes

import io.hamal.lib.common.serialization.serde.HotObjectModule
import io.hamal.lib.common.serialization.HotModule
import io.hamal.lib.common.serialization.JsonAdapters
import io.hamal.lib.common.serialization.JsonFactoryBuilder
import io.hamal.lib.common.value.TypesystemHotModule
import io.hamal.lib.domain.Json
import io.hamal.lib.domain.vo.ValueVariableJsonModule
import io.hamal.lib.nodes.control.Control
import io.hamal.lib.nodes.control.ControlExtension
import io.hamal.lib.nodes.control.ControlIdentifier
import io.hamal.lib.nodes.control.ControlType


object NodesHotModule : HotModule() {
    init {
        this[ConnectionId::class] = JsonAdapters.SnowflakeIdVariable(::ConnectionId)

        this[ControlIdentifier::class] = JsonAdapters.StringVariable(::ControlIdentifier)
        this[ControlType::class] = JsonAdapters.StringVariable(::ControlType)
        this[Control::class] = Control.Adapter
        this[ControlExtension::class] = ControlExtension.Adapter

        this[NodeId::class] = JsonAdapters.SnowflakeIdVariable(::NodeId)
        this[NodeType::class] = JsonAdapters.StringVariable(::NodeType)
        this[NodeTitle::class] = JsonAdapters.StringVariable(::NodeTitle)

        this[PortId::class] = JsonAdapters.SnowflakeIdVariable(::PortId)
    }
}


val json = Json(
    JsonFactoryBuilder()
        .register(HotObjectModule)
        .register(ValueVariableJsonModule)
        .register(NodesHotModule)
        .register(TypesystemHotModule)
)