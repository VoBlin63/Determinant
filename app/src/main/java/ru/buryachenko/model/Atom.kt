package ru.buryachenko.model

data class Atom(val row: Int, val col: Int) {
    var number: Int = 0

    val getNumber: Char
        get() = if (number == 0) ' ' else number.toChar()
}