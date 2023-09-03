package io.hamal.backend.filter

import io.hamal.lib.domain.vo.AuthToken
import io.hamal.repository.api.AuthRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.autoconfigure.security.SecurityProperties.BASIC_AUTH_ORDER
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
@Order(BASIC_AUTH_ORDER)
class AuthFilter(
    private val authRepository: AuthRepository
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.servletPath

        // FIXME skip worker auth for now
        if (path == "/v1/dequeue") {
            return filterChain.doFilter(request, response)
        }

        if (path == "/v1/sign-in" || path == "/v1/accounts" && request.method == "POST") {
            return filterChain.doFilter(request, response)
        }

        val token = request.getHeader("X-Hamal-Token")
            ?.let(::AuthToken) ?: throw NoSuchElementException("Account not found")

        val auth = authRepository.get(token)
        // FIXME not expired
        return filterChain.doFilter(request, response)
    }

}