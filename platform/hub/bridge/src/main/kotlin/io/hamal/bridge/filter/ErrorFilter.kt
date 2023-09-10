package io.hamal.bridge.filter

import io.hamal.lib.sdk.hub.HubError
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpServletResponse.*
import kotlinx.serialization.json.Json
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@Component
@Order(HIGHEST_PRECEDENCE)
class ErrorBridgeFilter(
    private val json: Json
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (t: Throwable) {

            val toHandle = when (t) {
                is ServletException -> t.cause
                else -> t
            }

            val statusCode = when (toHandle) {
                is IllegalArgumentException, is MethodArgumentTypeMismatchException -> SC_BAD_REQUEST
                is NoSuchElementException -> SC_NOT_FOUND
                else -> SC_INTERNAL_SERVER_ERROR
            }

            val encoded = json.encodeToString(HubError.serializer(), HubError(toHandle?.message ?: "Unknown error"))
            response.status = statusCode
            response.addHeader("Content-Type", "application/json")
            response.writer.write(encoded)
        }
    }

}