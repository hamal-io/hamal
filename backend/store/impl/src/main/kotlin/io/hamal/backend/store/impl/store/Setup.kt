package io.hamal.backend.store.impl.store

//internal fun DefaultJobDefinitionStore.setupSqlite() {
//    connection.createStatement().use {
//        it.execute("""PRAGMA journal_mode = wal;""")
//        it.execute("""PRAGMA locking_mode = exclusive;""")
//        it.execute("""PRAGMA temp_store = memory;""")
//        it.execute("""PRAGMA synchronous = off;""")
//    }
//}

//internal fun DefaultJobDefinitionStore.clear() {
////    lock.withLock {
//        connection.createStatement().use {
//            it.execute("DELETE FROM triggers")
//            it.execute("DELETE FROM job_definitions")
//            it.execute("DELETE FROM sqlite_sequence")
//        }
//        connection.commit()
////    }
//}

//internal fun DefaultJobDefinitionStore.setupSchema() {
//    lock.withLock {
//        connection.createStatement().use {
//            it.execute("""DROP TABLE IF EXISTS job_definitions;""")
//            it.execute("""DROP TABLE IF EXISTS triggers;""")
//            it.execute(
//                """
//            CREATE TABLE IF NOT EXISTS job_definitions (
//                id          INTEGER PRIMARY KEY,
//                version     INTEGER NOT NULL ,
//                request_id  BIGINT  NOT NULL,
//                reference   TEXT NOT NULL ,
//                inputs      BLOB,
//                secrets     BLOB,
//                instant     DATETIME NOT NULL,
//                UNIQUE (request_id)
//            );
//        """.trimIndent()
//            )
//            it.execute(
//                """
//           CREATE TABLE IF NOT EXISTS triggers(
//                id INTEGER PRIMARY KEY,
//                job_definition_id INTEGER NOT NULL,
//                type INTEGER NOT NULL,
//                inputs BLOB,
//                secrets BLOB,
//                data BLOB
//            );""".trimIndent()
//            )
//        }
//    }
//}