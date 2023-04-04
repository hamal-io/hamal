package io.hamal.application.adapter


import org.junit.jupiter.api.*


class UseCaseRegistryAdapterIT {

    @Test
    fun `fails`(){
        throw Error("xzy")
    }

}