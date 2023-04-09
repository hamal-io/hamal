package io.hamal.module.bus.api.common

import io.hamal.lib.ddd.base.ValueObject

class TopicId(value: Int) : ValueObject.ComparableImpl<Int>(value)
class TopicName(value: String) : ValueObject.ComparableImpl<String>(value)

class Topic(
    val id: TopicId,
    val type: Type,
    val name: TopicName
) {
    enum class Type {
        BusTopic,
        SystemTopic,
        UserTopic
    }

    override fun toString(): String {
        return "Topic($id,$type,$name)"
    }
    
}