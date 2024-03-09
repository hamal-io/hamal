package io.hamal.bridge.http.filter

import io.hamal.core.security.SecurityContext
import io.hamal.repository.api.Auth
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.autoconfigure.security.SecurityProperties.BASIC_AUTH_ORDER
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
@Order(BASIC_AUTH_ORDER)
class AuthBridgeFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // FIXME implement me
        return SecurityContext.with(Auth.Runner) { filterChain.doFilter(request, response) }
    }

}