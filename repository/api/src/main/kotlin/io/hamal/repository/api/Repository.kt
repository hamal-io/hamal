package io.hamal.repository.api

import io.hamal.lib.domain.vo.RegionId

interface Repository {
    val regionId: RegionId
}

interface ReadonlyRepository : Repository {

}