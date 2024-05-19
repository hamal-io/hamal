package io.hamal.lib.nodes

import io.hamal.lib.common.serialization.Serde
import io.hamal.lib.common.serialization.SerdeModuleJson
import io.hamal.lib.common.value.serde.SerdeModuleValueJson
import io.hamal.lib.common.value.serde.ValueVariableAdapters
import io.hamal.lib.domain.vo.SerdeModuleValueVariable


object NodesHotModule : SerdeModuleJson() {
    init {
        this[ConnectionIndex::class] = ValueVariableAdapters.Number(::ConnectionIndex)

        this[ControlIndex::class] = ValueVariableAdapters.Number(::ControlIndex)
        this[ControlKey::class] = ValueVariableAdapters.String(::ControlKey)
        this[ControlType::class] = ValueVariableAdapters.String(::ControlType)
        this[Control::class] = Control.Adapter
        this[TemplateControl::class] = TemplateControl.Adapter

        this[NodeIndex::class] = ValueVariableAdapters.Number(::NodeIndex)
        this[NodeType::class] = ValueVariableAdapters.String(::NodeType)
        this[NodeTitle::class] = ValueVariableAdapters.String(::NodeTitle)

        this[PortIndex::class] = ValueVariableAdapters.Number(::PortIndex)
        this[PortKey::class] = ValueVariableAdapters.String(::PortKey)
    }
}


val serde = Serde.json()
    .register(SerdeModuleValueJson)
    .register(SerdeModuleValueVariable)
    .register(NodesHotModule)