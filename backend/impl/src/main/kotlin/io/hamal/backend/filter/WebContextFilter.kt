package io.hamal.backend.filter

import io.hamal.backend.WebContext
import io.hamal.backend.WebContextData
import io.hamal.backend.repository.api.domain.ActiveTenant
import io.hamal.lib.common.Partition
import io.hamal.lib.domain.vo.TenantId
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class WebContextFilter(
    @Autowired val webContext: WebContext
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        webContext.set(
            WebContextData(
                tenant = ActiveTenant(TenantId(1)),
                partition = Partition(1)
            )
        )
        filterChain.doFilter(request, response)
    }
}