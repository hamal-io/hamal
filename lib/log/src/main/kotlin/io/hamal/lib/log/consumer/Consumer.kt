package io.hamal.lib.log.consumer

import java.nio.ByteBuffer

interface Consumer {

    fun poll(): List<Consumer.Record>

    data class Record(
        val key: ByteBuffer,
        val value: ByteBuffer
    )
}


class DefaultConsumer : Consumer{
    override fun poll(): List<Consumer.Record> {
        TODO("Not yet implemented")
    }

}