package io.hamal.lib.nodes

import io.hamal.lib.common.serialization.Serde
import io.hamal.lib.common.serialization.SerdeModuleJson
import io.hamal.lib.common.value.serde.SerdeModuleValueJson
import io.hamal.lib.common.value.serde.ValueVariableAdapters
import io.hamal.lib.domain.vo.SerdeModuleValueVariable


object NodesHotModule : SerdeModuleJson() {
    init {
        this[ConnectionId::class] = ValueVariableAdapters.SnowflakeId(::ConnectionId)

        this[ControlId::class] = ValueVariableAdapters.SnowflakeId(::ControlId)
        this[ControlKey::class] = ValueVariableAdapters.String(::ControlKey)
        this[ControlType::class] = ValueVariableAdapters.String(::ControlType)
        this[Control::class] = Control.Adapter
        this[TemplateControl::class] = TemplateControl.Adapter

        this[NodeId::class] = ValueVariableAdapters.SnowflakeId(::NodeId)
        this[NodeType::class] = ValueVariableAdapters.String(::NodeType)
        this[NodeTitle::class] = ValueVariableAdapters.String(::NodeTitle)

        this[PortId::class] = ValueVariableAdapters.SnowflakeId(::PortId)
    }
}


val serde = Serde.json()
    .register(SerdeModuleValueJson)
    .register(SerdeModuleValueVariable)
    .register(NodesHotModule)