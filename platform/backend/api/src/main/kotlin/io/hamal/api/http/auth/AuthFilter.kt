package io.hamal.api.http.auth

import io.hamal.lib.domain.vo.AuthToken
import io.hamal.repository.api.AccountQueryRepository
import io.hamal.repository.api.AuthRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.security.SecurityProperties.BASIC_AUTH_ORDER
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter


private val log = LoggerFactory.getLogger(AuthApiFilter::class.java)

@Component
@Order(BASIC_AUTH_ORDER)
class AuthApiFilter(
    private val authRepository: AuthRepository,
    private val accountQueryRepository: AccountQueryRepository
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.servletPath

        if (request.method == "OPTIONS") {
            return filterChain.doFilter(request, response)
        }

        if (path == "/v1/login" && request.method == "POST") {
            return filterChain.doFilter(request, response)
        }

        if (path == "/v1/anonymous-accounts" && request.method == "POST") {
            return filterChain.doFilter(request, response)
        }

        val token = request.getHeader("authorization")
            ?.replace("Bearer ", "")
            ?.let(::AuthToken)
            ?: run {
                log.warn("Unauthorized request on $path")
                throw IllegalCallerException("Forbidden")
            }

        val auth = authRepository.find(token) ?: run {
            log.warn("Unauthorized request on $path")
//            throw NoSuchElementException("Auth not found")
            throw IllegalCallerException("Forbidden")
        }

        AuthContextHolder.set(AuthContext(auth, accountQueryRepository.get(auth.accountId), token))

        try {
            return filterChain.doFilter(request, response)
        } finally {
            AuthContextHolder.clear()
        }
    }
}