package io.hamal.lib.common.domain


interface DomainObject<ID : DomainId> {
    val id: ID
    val partition get() = id.partition()
    //val createdAt = DomainCreatedAt(id.value.elapsed())
    //val updateAt: DomainUpdatedAt
}