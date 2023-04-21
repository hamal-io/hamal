package io.hamal.lib.log

import java.sql.DriverManager
import java.sql.Statement




fun main(args: Array<String>) {

    var connection = DriverManager.getConnection("jdbc:sqlite:/tmp/log/partition-0001.db")
    var statement: Statement = connection.createStatement()
    statement.setQueryTimeout(30) // set timeout to 30 sec.

    statement.executeUpdate("drop table if exists records")
    statement.executeUpdate("create table records (key integer, value string)")

    statement.execute("PRAGMA journal_mode = wal;")
    statement.execute("PRAGMA synchronous = off;")
    statement.execute("PRAGMA cache_size = 1000000;")
    statement.execute("PRAGMA locking_mode = EXCLUSIVE;")
    statement.execute("PRAGMA temp_store = MEMORY; ")


    val start = System.currentTimeMillis()
    for (i in 0..100_000) {
        statement.execute("insert into records values($i, 'msg_$i')");
    }
    statement.close()
    connection.close()
    println("TOOK: ${(System.currentTimeMillis() - start)}")

}