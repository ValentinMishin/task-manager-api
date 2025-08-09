package ru.valentin.ktl.class_object
import java.time.LocalDate
import java.time.LocalDateTime

//top class declaration only
const val SUBSYSTEM_DEPRECATED: String = "This subsystem is deprecated"

fun main() {
    val mt = MyMyTask("", LocalDate.now(), LocalDateTime.now())
    val upd = mt.updatedAt
    mt.updatedAt = LocalDateTime.now().plusDays(1)
}

class MyMyTask(val title: String,
    val dueDate: LocalDate,
    val createdAt: LocalDateTime) {

    var desc: String? = null
//        @Inject set

    var updatedAt: LocalDateTime? = null
        //вычисляемое значение
        get() = LocalDateTime.now().plusDays(1)
        set(value) {
            value?.year?.let {
                if (it >= 2025)
                    field = value
            }
        }
 }