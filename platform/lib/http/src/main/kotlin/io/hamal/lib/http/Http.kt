package io.hamal.lib.http

import io.hamal.lib.http.HttpRequest.HttpMethod
import io.hamal.lib.http.HttpRequest.HttpMethod.*
import org.apache.http.impl.client.HttpClientBuilder

interface HttpTemplate {
    fun delete(url: String = ""): HttpRequest
    fun get(url: String = ""): HttpRequest
    fun patch(url: String = ""): HttpRequestWithBody
    fun post(url: String = ""): HttpRequestWithBody
    fun put(url: String = ""): HttpRequestWithBody

    val baseUrl: String
}

class HttpTemplateImpl(
    override var baseUrl: String = "",
    private val headerFactory: HttpMutableHeaders.() -> Unit = {},
    private var serdeFactory: HttpSerdeFactory = DefaultHttpSerdeFactory,
    private var httpClientFactory: HttpClientBuilder.() -> Unit = {}
) : HttpTemplate {
    override fun delete(url: String): HttpRequest = requestWith(Delete, url)
    override fun get(url: String): HttpRequest = requestWith(Get, url)
    override fun patch(url: String): HttpRequestWithBody = requestWith(Patch, url)
    override fun post(url: String): HttpRequestWithBody = requestWith(Post, url)
    override fun put(url: String): HttpRequestWithBody = requestWith(Put, url)
    private fun requestWith(method: HttpMethod, url: String): HttpRequestImpl {
        return HttpRequestImpl(
            method = method,
            url = baseUrl + url,
            headers = HttpMutableHeaders().apply(headerFactory),
            serdeFactory = serdeFactory,
            client = HttpClientBuilder.create().apply(httpClientFactory).build()
        )
    }
}

//fun HttpTemplate.delete(url: ValueString): HttpRequest = delete(url.stringValue)
//fun HttpTemplate.get(url: ValueString): HttpRequest = get(url.stringValue)
//fun HttpTemplate.patch(url: ValueString): HttpRequest = patch(url.stringValue)
//fun HttpTemplate.post(url: ValueString): HttpRequest = post(url.stringValue)
//fun HttpTemplate.put(url: ValueString): HttpRequest = put(url.stringValue)