package ru.valentin.ktl.class_object

fun main() {

}

interface Property<T> {
    fun customGet(): T

    fun withDefault() {
        println()
    }
}

class Simple : Property<String> {
    override fun customGet(): String {
        withDefault() //
        return "asd"
    }
}

class VerySimple {
    fun task() {

    }
}

interface Named {
    val name: String
}

interface Personnn : Named {
    val firstName: String
    val lastName: String

    override val name: String get() = "$firstName $lastName"
}

data class Employee(
    // implementing 'name' is not required
    override val firstName: String,
    override val lastName: String,
) : Personnn