package io.hamal.bootstrap

import org.springframework.test.context.ActiveProfiles


@ActiveProfiles("memory")
class SqliteHamalTest : BaseHamalTest()