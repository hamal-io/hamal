package io.hamal.backend.filter

import io.hamal.backend.repository.api.domain.Tenant.Active
import io.hamal.lib.domain.vo.AccountId
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class TenantFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        request.setAttribute("tenant", Active(AccountId(1)))
        filterChain.doFilter(request, response)
    }

}