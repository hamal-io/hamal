package com.nyanbot

import com.nyanbot.http.controller.FlowTriggerRegistryItem
import com.nyanbot.repository.FlowTriggerType
import io.hamal.lib.domain.vo.TopicName

object FlowTriggerRegistry {

    operator fun get(type: FlowTriggerType): FlowTriggerRegistryItem {
        return data[type] ?: throw NoSuchElementException("Trigger not found")
    }

    val data = mapOf(
        FlowTriggerType("v1_web3_new_lp_pair") to FlowTriggerRegistryItem(
            type = FlowTriggerType("v1_web3_new_lp_pair"),
            topicName = TopicName("v1_web3_new_lp")
        )
    )
}