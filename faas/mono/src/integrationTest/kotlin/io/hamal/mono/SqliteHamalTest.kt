package io.hamal.mono

import org.springframework.test.context.ActiveProfiles


@ActiveProfiles("memory")
class SqliteHamalTest : BaseHamalTest()