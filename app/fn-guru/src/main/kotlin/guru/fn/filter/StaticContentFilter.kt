package guru.fn.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

// Based on: https://stackoverflow.com/a/72466110
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class StaticContentFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        doFilter(request, response, filterChain)
    }

    private fun doFilter(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val path = request.servletPath
        if (path.startsWith("/v1")) {
            chain.doFilter(request, response)
        } else if (fileExtensions.stream().anyMatch { path.contains(it) }) {
            resourceToResponse("dist$path", response)
        } else {
            resourceToResponse("dist/index.html", response)
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

        response.setHeader("Cache-Control", "private, max-age=604800")
        response.outputStream.write(inputStream.readAllBytes())
    }

    private val fileExtensions: List<String> = mutableListOf(
        "html", "js", "json", "csv", "css", "png",
        "svg", "eot", "ttf", "woff", "appcache",
        "jpg", "jpeg", "gif", "ico"
    )
}