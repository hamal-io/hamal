package io.hamal.lib.web3.evm.abi

import io.hamal.lib.web3.evm.abi.type.EvmHash
import io.hamal.lib.web3.evm.abi.type.EvmPrefixedHexString
import io.hamal.lib.web3.evm.abi.type.EvmType


data class EvmEvent<INDEXED : EvmOutputTuple, NOT_INDEXED : EvmOutputTuple>(
    val name: String,
    val indexed: INDEXED,
    val notIndexed: NOT_INDEXED
) {
    val signature = EvmSignature(
        name + '(' + indexed.concatenatedTypes() + ',' + notIndexed.concatenatedTypes() + ')'
    )

    fun decode(
        topics: List<EvmHash>,
        data: EvmPrefixedHexString
    ): List<DecodedEvmType<*>> {
        require(topics.isNotEmpty()) { "Topics must not be empty" }

        val eventTopic = topics[0]
        require(topicMatchesSignature(eventTopic)) { "Unable to decode event from different topic" }

        val result = mutableListOf<DecodedEvmType<*>>()
        if (topics.size > 1) {
            val remainingTopics = topics.subList(1, topics.size)
            val topicData = EvmPrefixedHexString("0x${
                remainingTopics.joinToString("") {
                    it.toHexString().toString()
                }
            }")
            result.addAll(indexed.decode(topicData))
        }
        result.addAll(notIndexed.decode(data))
        return result
    }

    fun decodeToMap(topics: List<EvmHash>, data: EvmPrefixedHexString): Map<String, EvmType<*>> {
        return decode(topics, data).associateBy({ it.name }, { it.value })
    }

    private fun topicMatchesSignature(topic: EvmHash) = signature.encoded == topic
}