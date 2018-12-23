package ru.buryachenko.model

import android.graphics.Color
import android.util.Log
import ru.buryachenko.SIZE
import java.lang.StringBuilder
import java.util.function.BinaryOperator

class AtomMatrix {
    val matrix =  ArrayList<Atom>(SIZE*SIZE)
//    val possibleNumbers = ArrayList<MutableSet<Int>>(SIZE*SIZE)

    init {
        for (row in 1..SIZE) {
            for (col in 1..SIZE) {
                matrix.add(Atom(row, col))
                print(" ${quadrant(row,col)} ")
            }
            println("")
        }
    }


    fun get(row: Int, col: Int): Atom {
        matrix.forEach { if ((it.col == col) && (it.row == row)) return it}
        return Atom(0,0)
    }

    fun getPossible(row: Int, col: Int): MutableSet<Int> {
        val res = fullStackNumbers()
        val thisQuadrant = quadrant(row, col)
        matrix.filter { it.number > 0 }.forEach {
            if (((it.col == col) || (it.row == row)))
                res.remove(it.number)
            if (thisQuadrant == quadrant(it.row, it.col))
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
        fullStackNumbers().sortedBy { it }.forEach { tmp = tmp.append(if (possible.contains(it)) it.toString() else " " ) }
        return tmp.toString()
    }

    fun possibleStr(atom: Atom ) = possibleStr(getPossible(atom.row, atom.col))

    private fun fullStackNumbers(): MutableSet<Int> {
        val res = HashSet<Int>()
        for (i in 1..SIZE) res.add(i)
        return res
    }

    fun quadrant(row: Int, col: Int):Int {
        val res = (col-1) / 3 + ((row-1)/3)*(SIZE/3)
        return res
    }

    fun aloneNumber(atom: Atom): Int {
        var res = 0
        if (atom.number == 0) {
            val possibleList = getPossible(atom.row, atom.col)
            val localMatrix = matrix.filter {
                (quadrant(it.row, it.col) == quadrant(atom.row, atom.col)) && ((it.row != atom.row) && (it.col != atom.col))
            }
            possibleList.forEach { ourNum ->
                var existInOthers = false
                localMatrix.forEach { if (ourNum in getPossible(it.row, it.col)) existInOthers = true }
                if (!existInOthers)
                    res = ourNum
                //может быть только один
            }
        }
        return res
    }

}
