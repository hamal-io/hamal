package io.hamal.lib.domain._enum

import kotlin.reflect.full.companionObject
import kotlin.reflect.full.declaredMemberProperties

class NamespaceFeature private constructor(val name: String, val value: Int) : Comparable<NamespaceFeature> {
    override fun toString(): String = name
    override fun compareTo(other: NamespaceFeature): Int = value.compareTo(other.value)

    companion object {
        val SCHEDULES = NamespaceFeature("Schedules", 0)
        val TOPICS = NamespaceFeature("Topics", 1)
        val WEBHOOKS = NamespaceFeature("Webhooks", 2)
        val ENDPOINTS = NamespaceFeature("Endpoints", 3)
    }

    fun of(value: Int): NamespaceFeature {
        val members =
            NamespaceFeature::class.companionObject!!.declaredMemberProperties.filterIsInstance<NamespaceFeature>()
        return members.find { it.value == value }
            ?: throw IllegalArgumentException("$value not mapped as a namespace feature")
    }
}



