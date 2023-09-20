package io.hamal.lib.sdk.admin

import io.hamal.lib.http.HttpRequest
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

class TestTemplate(
    private val base: HttpTemplate,
) {
    fun injectURL(url: String): TestTemplate {
        val baseUrlProperty = base::class.declaredMemberProperties.find { it.name == "baseUrl" } as? KMutableProperty<*>
        baseUrlProperty?.isAccessible = true
        baseUrlProperty?.setter?.call(base, url)
        return this
    }

    fun getReq(url: String): HttpRequest {
        return base.get(url)
    }
}

interface AdminMetricService {
    fun get(): MetricData
}

internal class DefaultAdminMetricService(
    private val template: HttpTemplate
) : AdminMetricService {

    override fun get(): MetricData {
        val myTemplate = TestTemplate(template).injectURL("http://localhost:9009")
        val res = myTemplate.getReq("/v1/metric/json").execute()
        return res.fold(MetricData::class)
    }

    /*
    ?????
    override fun get() = template.get("/v1/metrics/json")
         .header("Content-Type", "application/json")
         .execute()
         .fold(MetricData::class)
   */
}
