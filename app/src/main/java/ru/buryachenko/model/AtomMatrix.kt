package ru.buryachenko.model

import android.util.Log
import ru.buryachenko.SIZE
import java.lang.StringBuilder

class AtomMatrix {
    val matrix =  ArrayList<Atom>(SIZE*SIZE)
    val possibleNumbers = ArrayList<MutableSet<Int>>(SIZE*SIZE)


    init {
        for (row in 1..SIZE)
            for (col in 1..SIZE)
                matrix.add(Atom(row, col))
        setNumber(1,3,5)
        setNumber(1,4,3)
    }


    fun get(row: Int, col: Int): Atom {
        matrix.forEach { if ((it.col == col) && (it.row == row)) return it}
        return Atom(0,0)
    }

    fun getPossible(row: Int, col: Int): MutableSet<Int> {
        val res = fullStackNumbers()
        matrix.forEach {
            if ((it.number > 0)
                && ((it.col == col) || (it.row == row)))
                res.remove(it.number)
        }
        return res
    }

    fun setNumber(row: Int, col: Int, value: Int) {
        if ((value == 0) || (value in getPossible(row,col))) {
            get(row,col).number = value
        }
    }

    fun printOut() {
        for (row in 1..SIZE) {
            var tmp = StringBuilder("")
            for (col in 1..SIZE)
                tmp = tmp.append(" ($row,$col) = ${get(row, col).getNumber} [${possibleStr(getPossible(row,col))}]")
            println(tmp)
        }
    }

    fun possibleStr(possible: Set<Int>): String {
        var tmp = StringBuilder("")
        fullStackNumbers().forEach { tmp = tmp.append(if (possible.contains(it)) it.toString() else " " ) }
        return tmp.toString()
    }

    private fun fullStackNumbers(): MutableSet<Int> {
        val res = HashSet<Int>()
        for (i in 1..SIZE) res.add(i)
        return res
    }
}