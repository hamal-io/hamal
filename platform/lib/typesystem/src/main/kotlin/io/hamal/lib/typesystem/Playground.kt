package io.hamal.lib.typesystem

import io.hamal.lib.typesystem.Field.Kind
import io.hamal.lib.typesystem.value.ValueObject
import io.hamal.lib.typesystem.value.ValueString
import io.hamal.lib.typesystem.value.forType
import java.time.LocalDate


fun main() {

    val Entity = Type(
        "entity",
        Field(Kind.String, "id")
    )

    val Pupil = Type(
        "pupil",
        Field(Kind.String, "first_name"),
        Field(Kind.String, "last_name"),
        Field(Kind.Date, "dob")
    ) extends Entity

    val Another = Type(
        "another",
        Field(Kind.String, "answer")
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

//    val PupilList = Type("pupilList", Field(Kind.List, "pupils", valueType = Pupil))

    val PupilList = TypeList("pupil_list", Pupil)

    fun ValueObject.fullname(): ValueString = forType(Pupil) {
        ValueString("${get<ValueString>("last_name")}, ${get<ValueString>("first_name")}")
    }

    val list = PupilList(bob, suzy)

    val a = Another("xyz")

    println(list)

    println(bob.fullname())
//    println(a.fullname())

}