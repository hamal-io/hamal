package io.hamal.lib.sdk.admin

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.sdk.fold
import kotlinx.serialization.Serializable
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

@Serializable
data class MetricData(
    val time: Long = System.currentTimeMillis(),
    val events: MutableList<Count> = mutableListOf()
) {
    @Serializable
    data class Count(
        val name: String,
        var value: Int
    )
}

interface AdminMetricService {
    fun get(): MetricData
}

internal class DefaultAdminMetricService(
    private val template: HttpTemplate
) : AdminMetricService {

    private fun swapPortandGet(base: HttpTemplate, injectUrl: String, getReq: String): MetricData {
        val baseUrlProperty = base::class.declaredMemberProperties.find { it.name == "baseUrl" } as? KMutableProperty<*>
        baseUrlProperty?.isAccessible = true
        val temp = baseUrlProperty?.getter?.call(base)
        baseUrlProperty?.setter?.call(base, injectUrl)
        val res = base.get(getReq).execute().fold(MetricData::class)
        baseUrlProperty?.setter?.call(base, temp)
        baseUrlProperty?.isAccessible = false
        return res
    }

    override fun get(): MetricData {
        return swapPortandGet(template, "http://localhost:9009", "/v1/metrics/json")
    }

    /*  that should work but wrong port
        override fun get() = template.get("/v1/metrics/json")
             .header("Content-Type", "application/json")
             .execute()
             .fold(MetricData::class)
    */
}
