package io.hamal.lib.domain._enum


class NamespaceFeature private constructor(val name: String, val value: Int) : Comparable<NamespaceFeature> {
    override fun toString(): String = name
    override fun compareTo(other: NamespaceFeature): Int = value.compareTo(other.value)

    companion object {
        val SCHEDULES = NamespaceFeature("Schedules", 0)
        val TOPICS = NamespaceFeature("Topics", 1)
        val WEBHOOKS = NamespaceFeature("Webhooks", 2)
        val ENDPOINTS = NamespaceFeature("Endpoints", 3)
    }
}



