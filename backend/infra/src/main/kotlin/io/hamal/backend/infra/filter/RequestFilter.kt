package io.hamal.backend.infra.filter

import io.hamal.lib.domain.ReqId
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.concurrent.atomic.AtomicInteger

@Component
class RequestFilter : OncePerRequestFilter() {

    private val counter = AtomicInteger(0)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        request.setAttribute("requestId", ReqId(counter.incrementAndGet().toBigInteger()))
        filterChain.doFilter(request, response)
    }

}