package io.hamal.lib.typesystem

import io.hamal.lib.typesystem.type.*
import io.hamal.lib.typesystem.value.ValueNumber
import io.hamal.lib.typesystem.value.ValueObject
import io.hamal.lib.typesystem.value.ValueString
import io.hamal.lib.typesystem.value.forType
import java.time.LocalDate


fun main() {

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

    val PupilList = TypeListObject("pupilList", Pupil)

//    val PupilList = TypeList("pupil_list", Pupil)

    fun ValueObject.fullname(): ValueString = forType(Pupil) {
        ValueString("${get<ValueString>("last_name")}, ${get<ValueString>("first_name")}")
    }

    val list = PupilList(bob, suzy)

    val a = Another("xyz")

    println(list)

    println(bob.fullname())
//    println(a.fullname())

    val numberList = TypeListNumber(ValueNumber(12), ValueNumber(323), ValueNumber(4))

    println(numberList)
}