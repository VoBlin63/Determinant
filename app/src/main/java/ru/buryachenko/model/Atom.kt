package ru.buryachenko.model

import ru.buryachenko.SIZE
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.HashSet

data class Atom(val row: Int, val col: Int) {
    var number: Int = 0
    var auto = false
    val bannedFrom = HashMap<Int, Int>()

    val quadrant
        get() = quadrant(row, col)

    val possible: HashSet<Int>
        get() {
            var res = fullStack()
            bannedFrom.forEach { res.remove(it.value)}
            return res
        }

    fun reset() {
        number = 0
        bannedFrom.clear()
    }

    val possibleStr
        get():String {
            var tmp = StringBuilder("")
            if (possible.size == SIZE)
                tmp = StringBuilder("         ")
            else
                possible.sortedBy { it }.forEach {tmp = tmp.append("$it")}
            return tmp.toString()
        }

    val id
        get() = makeId(row, col)

    init {
        reset()
    }
}

fun quadrant(row: Int, col: Int) = (col - 1) / 3 + ((row - 1) / 3) * (SIZE / 3)
fun makeId(row: Int, col: Int) = 1000 + row * 100 + col
fun fullStack(): HashSet<Int> {
    val res = HashSet<Int>()
    for (i in 1..SIZE) res.add(i)
    return res
}
