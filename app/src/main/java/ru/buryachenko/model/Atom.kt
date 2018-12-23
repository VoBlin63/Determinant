package ru.buryachenko.model

import ru.buryachenko.SIZE
import java.lang.StringBuilder

data class Atom(val row: Int, val col: Int) {
    var number: Int = 0
    val possible = HashSet<Int>()
    val quadrant
        get() = quadrant(row, col)

    fun reset() {
        number = 0
        possible.clear()
        for (i in 1..SIZE) possible.add(i)
    }

    val possibleStr
        get():String {
            var tmp = StringBuilder("")
            possible.forEach {tmp = tmp.append("$it")}
            return tmp.toString()
        }

    init {
        reset()
    }
}

fun quadrant(row: Int, col: Int) = (col - 1) / 3 + ((row - 1) / 3) * (SIZE / 3)
fun makeId(row: Int, col: Int) = 1000 + row * 100 + col
fun defineRow(id: Int) = (id - 1000) / 100
fun defineCol(id: Int) = (id - 1000) % 100
fun fullStackNumbers() = Atom(0,0).possible

