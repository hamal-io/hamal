package io.hamal.frontend.filter

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

// Based on: https://stackoverflow.com/a/72466110
@Component
class StaticContentFilter : Filter {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        doFilter(request as HttpServletRequest, response as HttpServletResponse, chain)
    }

    private fun doFilter(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val path = request.servletPath
        if (path.startsWith("/v1")) {
            chain.doFilter(request, response)
        } else if (fileExtensions.stream().anyMatch { path.contains(it) }) {
            resourceToResponse("static$path", response)
        } else {
            resourceToResponse("static/index.html", response)
        }
    }

    private fun resourceToResponse(resourcePath: String, response: HttpServletResponse) {
        val inputStream = Thread.currentThread().contextClassLoader.getResourceAsStream(resourcePath)

        if (inputStream == null) {
            response.sendError(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.reasonPhrase)
            return
        }

        if (resourcePath.endsWith(".html")) {
            response.contentType = "text/html"
        }
        if (resourcePath.endsWith(".css")) {
            response.contentType = "text/css"
        }
        if (resourcePath.endsWith(".js")) {
            response.contentType = "text/javascript"
        }

        inputStream.transferTo(response.outputStream)
    }

    private val fileExtensions: List<String> = mutableListOf(
        "html", "js", "json", "csv", "css", "png",
        "svg", "eot", "ttf", "woff", "appcache",
        "jpg", "jpeg", "gif", "ico"
    )
}