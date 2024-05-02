package io.hamal.lib.nodes

import io.hamal.lib.common.serialization.GsonFactoryBuilder
import io.hamal.lib.common.serialization.SerializationModule
import io.hamal.lib.common.serialization.json.SerdeModule
import io.hamal.lib.common.value.ValueJsonModule
import io.hamal.lib.common.value.ValueJsonAdapters
import io.hamal.lib.domain.Json
import io.hamal.lib.domain.vo.ValueVariableJsonModule
import io.hamal.lib.nodes.control.Control
import io.hamal.lib.nodes.control.ControlExtension
import io.hamal.lib.nodes.control.ControlIdentifier
import io.hamal.lib.nodes.control.ControlType


object NodesHotModule : SerializationModule() {
    init {
        this[ConnectionId::class] = ValueJsonAdapters.SnowflakeIdVariable(::ConnectionId)

        this[ControlIdentifier::class] = ValueJsonAdapters.StringVariable(::ControlIdentifier)
        this[ControlType::class] = ValueJsonAdapters.StringVariable(::ControlType)
        this[Control::class] = Control.Adapter
        this[ControlExtension::class] = ControlExtension.Adapter

        this[NodeId::class] = ValueJsonAdapters.SnowflakeIdVariable(::NodeId)
        this[NodeType::class] = ValueJsonAdapters.StringVariable(::NodeType)
        this[NodeTitle::class] = ValueJsonAdapters.StringVariable(::NodeTitle)

        this[PortId::class] = ValueJsonAdapters.SnowflakeIdVariable(::PortId)
    }
}


val json = Json(
    GsonFactoryBuilder()
        .register(SerdeModule)
        .register(ValueVariableJsonModule)
        .register(NodesHotModule)
        .register(ValueJsonModule)
)