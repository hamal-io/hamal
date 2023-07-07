package io.hamal.lib.web3.eth.abi

import io.hamal.lib.web3.eth.abi.type.EthHash
import io.hamal.lib.web3.eth.abi.type.EthPrefixedHexString
import io.hamal.lib.web3.eth.abi.type.EthType

data class EthEvent<INDEXED : EthOutputTuple, NOT_INDEXED : EthOutputTuple>(
    val name: String,
    val indexed: INDEXED,
    val notIndexed: NOT_INDEXED
) {
    val signature = EthSignature(
        name + '(' + indexed.concatenatedTypes() + ',' + notIndexed.concatenatedTypes() + ')'
    )

    fun decode(
        topics: List<EthHash>,
        data: EthPrefixedHexString
    ): List<DecodedEthType<*>> {
        require(topics.isNotEmpty()) { "Topics must not be empty" }

        val eventTopic = topics[0]
        require(topicMatchesSignature(eventTopic)) { "Unable to decode event from different topic" }

        val result = mutableListOf<DecodedEthType<*>>()
        if (topics.size > 1) {
            val remainingTopics = topics.subList(1, topics.size)
            println()
            val topicData = EthPrefixedHexString("0x${
                remainingTopics.joinToString("") {
                    it.toHexString().toString()
                }
            }")
            result.addAll(indexed.decode(topicData))
        }
        result.addAll(notIndexed.decode(data))
        return result
    }

    fun decodeToMap(topics: List<EthHash>, data: EthPrefixedHexString): Map<String, EthType<*>> {
        return decode(topics, data).associateBy({ it.name }, { it.value })
    }

    private fun topicMatchesSignature(topic: EthHash) = signature.encoded == topic
}