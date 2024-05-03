package io.hamal.lib.nodes

import io.hamal.lib.common.serialization.GsonFactoryBuilder
import io.hamal.lib.common.serialization.Serde
import io.hamal.lib.common.serialization.SerdeModuleJson
import io.hamal.lib.common.serialization.json.SerdeModule
import io.hamal.lib.common.value.ValueJsonAdapters
import io.hamal.lib.common.value.SerdeModuleJsonValue
import io.hamal.lib.domain.vo.SerdeModuleJsonValueVariable
import io.hamal.lib.nodes.control.Control
import io.hamal.lib.nodes.control.ControlExtension
import io.hamal.lib.nodes.control.ControlIdentifier
import io.hamal.lib.nodes.control.ControlType


object NodesHotModule : SerdeModuleJson() {
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


val serde = Serde(
    GsonFactoryBuilder()
        .register(SerdeModule)
        .register(SerdeModuleJsonValue)
        .register(SerdeModuleJsonValueVariable)
        .register(NodesHotModule)
)