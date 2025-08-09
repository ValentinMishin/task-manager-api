package ru.valentin.ktl.class_object

fun main () {
    var hold = Hold("aaa")
    val testVa = TestPassValue(hold)
    hold.h = "bbb"
    hold = Hold("ccc")
}

class TestPassValue(val i: Hold) {}
data class Hold (var h: String) {}

// по дэфолту финал, для наследование надо open
open class Person(open val id: Int = 0, val name: String) {

    constructor(name: String) : this(id = 10, name) {

    }

    constructor(name: String, info: String) : this(name) {
        println(info)
    }
}
open class Employee : Person {
    //неявно переопределяется геттер
    override val id: Int = 10

    constructor(name: String, info: String) : super(name) {

    }
}

open class Empty {}
class Derived : Empty() {}

open class Test(s: String) {}
class DerivedTest : Test {
    constructor(s: String) : super(s)
}

open class Override {
    open fun method1() {}
    fun method2() {}
}
//можно убрать оупен чтобы методы стали финальными
open class DerOverride : Override() {
    //закрыть метод для переопределения
    final override fun method1() {
    }
}

interface Shape {
    val vertexCount: Int
}

class Rectangle(override val vertexCount: Int = 4) : Shape // Always has 4 vertices






