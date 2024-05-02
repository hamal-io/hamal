package io.hamal.lib.common.value

import io.hamal.lib.common.value.TypeArray.Companion.TypeArray
import java.time.LocalDate


fun main() {

    val No = TypeObject(
        "no",
        Field(TypeString, "answer")
    )

    val Entity = TypeObject(
        "entity",
        Field(TypeString, "id")
    )

    val Pupil = TypeObject(
        "pupil",
        Field(TypeString, "first_name"),
        Field(TypeString, "last_name"),
        Field(TypeDate, "dob")
    ) extends Entity

    val Another = TypeObject(
        "another",
        Field(TypeString, "answer")
    )

//    val Pupil = DType(
//        "pupil",
//        Field(Kind.String, "id", "Id"),
//        Field(Kind.String, "name", "Name"),
//        Field(Kind.Date, "dob", "Date of Birth")
//    )

    val bob = Pupil(
        "1",
        "Bob",
        "Schropp",
        LocalDate.of(1992, 7, 9),
    )

    val suzy = Pupil("2", "Suzy", "Yzus", LocalDate.of(1992, 8, 23))

    val PupilArray = TypeArray(Pupil, "pupilArray")
    val EntityArray = TypeArray(Entity, "entityArray")

    val l = EntityArray(bob, Another("xyz"), No("dasd"))
    println(l)

//    val PupilList = TypeList("pupil_list", Pupil)

    fun ValueObject.fullname(): ValueString = forType(Pupil) {
        ValueString("${get<ValueString>("last_name")}, ${get<ValueString>("first_name")}")
    }

    val list = PupilArray(bob, suzy)

    val a = Another("xyz")

    println(list)

    println(bob.fullname())
//    println(a.fullname())

//    val numberList = TypeArrayNumber(ValueNumber(12), ValueNumber(323), ValueNumber(4))
//
//    println(numberList)
}