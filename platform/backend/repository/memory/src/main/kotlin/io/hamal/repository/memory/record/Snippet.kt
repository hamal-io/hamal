package io.hamal.repository.memory.record

import io.hamal.lib.domain.vo.SnippetId
import io.hamal.repository.api.Snippet
import io.hamal.repository.api.SnippetCmdRepository.*
import io.hamal.repository.api.SnippetQueryRepository.*
import io.hamal.repository.api.SnippetRepository
import io.hamal.repository.record.snippet.SnippetRecord

internal object CurrentSnippetProjection {
    private val projection = mutableMapOf<SnippetId, Snippet>()
    fun apply(snippet: Snippet) {

    }

    fun find(snippetId: SnippetId): Snippet? = projection[snippetId]

    fun list(query: SnippetQuery): List<Snippet>{
        TODO()
    }

    fun count(query: SnippetQuery): ULong{
        TODO()
    }

    fun clear(){
        projection.clear()
    }
}

class MemorySnippetRepository : MemoryRecordRepository<SnippetId, SnippetRecord, Snippet>(

), SnippetRepository{
    override fun create(cmd: CreateCmd): Snippet {
        TODO("Not yet implemented")
    }

    override fun update(snippetId: SnippetId, cmd: UpdateCmd): Snippet {
        TODO("Not yet implemented")
    }

    override fun find(snippetId: SnippetId): Snippet? {
        TODO("Not yet implemented")
    }

    override fun list(query: SnippetQuery): List<Snippet> {
        TODO("Not yet implemented")
    }

    override fun count(query: SnippetQuery): ULong {
        TODO("Not yet implemented")
    }
}