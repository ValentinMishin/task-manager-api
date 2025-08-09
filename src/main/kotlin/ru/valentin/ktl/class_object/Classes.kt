package ru.valentin.ktl.class_object

import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime

/*
* первичный констр, инициализатор(init), инициализтор проперти вызываются перед вторичным конструктором
* модификаторы доступа доступны в конструкторах */

fun main () {
    val task = Task("main_task", "no desc")
    val task1 = Task("2 task", "no desc",
        Timestamp.valueOf(LocalDateTime.now()), "add")
}

/*primary constructor инициализирует экземляр и его проперти в заголовке*/
class Task constructor(title: String, /*простой параметр не проперти*/
                       /*декларация и инициализация property*/
                       val description: String?,
                       /*декларация и инициализация property c дефолтным значением*/
                       val dueDate: LocalDate = LocalDate.now().plusDays(1)
) {
    //параметр title из конструктора присвоили проперти title
    //часть '= title' - property initializer
    val title = title

    //часть первичного конструктора
    //initializer block
    init {
        println("parameter title is $title,\nproperty is ${this.title}\ninitializing...")
    }
    //property
    val createdAt: Timestamp = Timestamp.valueOf(LocalDateTime.now())
    var updatedAt: Timestamp? = null

    //

    //secondary constructor
    // ': this(name, description)' - явное делигирование первичному конструктору
    // вторичный констр неявно вызывает первчиный, даже если первичный не определен
    constructor(name: String,
                description: String?,
                updatedAt: Timestamp) : this(name, description) {
                    this.updatedAt = updatedAt
    }
    //вызывает другой вторичный конструктор - аналог цепочек конструкторов в джаве
    constructor(name: String,
                description: String?,
                updatedAt: Timestamp,
                additional: String) : this(name, description, updatedAt) {
        println(additional)
    }
}
//первичный приватный конструктор, необходим любой публичный для цепочки конструкторов
open class SimpleTask private constructor() {
    constructor(info: String) : this()
}
class MyTask(s: String) : SimpleTask(s) {}

//первичного конструктора нет this() не нужен
class ComplicatedTask {
    constructor(info: String) {}
}
//всегда создается пустой конструктор (для цепочки вызовов конструкторов и инициализации)
class EmptyTask {
    var title: String? = null
}

//открыт для расширения
//abstract class Shape {
//    abstract fun draw()
//    open fun info() {}
//}
//
//abstract class Rect : Shape() {
//    abstract override fun info()
//}