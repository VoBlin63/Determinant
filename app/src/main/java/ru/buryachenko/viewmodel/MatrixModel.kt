package ru.buryachenko.viewmodel

import android.arch.lifecycle.ViewModel
import android.util.Log
import ru.buryachenko.LOGTAG
import ru.buryachenko.SIZE
import ru.buryachenko.model.Atom
import ru.buryachenko.model.makeId

class MatrixModel : ViewModel() {
    val matrix = HashMap<Int, Atom>(SIZE * SIZE)

    init {
//        for (row in 1..SIZE)
//            for (col in 1..SIZE)
//                matrix[makeId(row, col)] = Atom(row, col)
        resetAll()
    }

    fun resetAll() {
        for (row in 1..SIZE)
            for (col in 1..SIZE)
                matrix[makeId(row, col)] = Atom(row, col)
    }

    fun clearAllAuto() {
        matrix.forEach {it.value.auto = false}
    }

    fun setNumber(id: Int, value: Int, auto: Boolean = false) {
        val element = matrix[id]
        element?.let {
            if (it.possible.contains(value)) {
//                matrix[id]!!.number = value
                element.number = value
                element.auto = auto
                matrix.values.forEach { atom ->
                    if ((atom.row == element.row) || (atom.col == element.col) || (atom.quadrant == element.quadrant))
                        atom.possible.remove(value)
                }
            }
        }
    }

    fun defineValue(atom: Atom): Int {
        var res = 0
        if (atom.number == 0) {
            when {
                atom.possible.size == 1 -> res = atom.possible.first()
                atom.possible.size > 1 -> {
                    val allPossibleThisQuadrant = HashSet<Int>()
                    matrix.values.forEach {
                        if ((it.number == 0) && (it.quadrant == atom.quadrant) && (it.id != atom.id))
                            it.possible.forEach { new-> allPossibleThisQuadrant.add(new) }
                    }

//                    if (atom.row == 3 && atom.col == 2)
//                    matrix.values.filter { it.number == 0 && it.quadrant == atom.quadrant }
//                        .forEach { Log.v(LOGTAG, " in (${it.row},${it.col}) possible ${it.possible}") }
//
//                    if (atom.row == 3 && atom.col == 2) {
//                        allPossibleThisQuadrant.forEach { Log.v(LOGTAG, "$it") }
//                        matrix.values.filter { it.number == 0 && it.quadrant == 0 }.forEach {
//                            if (it.possible.contains(5))
//                                Log.v(LOGTAG, "found 5 in (${it.row},${it.col}) ")
//                        }
//                    }

                    val results = ArrayList<Int>()
                    atom.possible.forEach {
                        if (it !in allPossibleThisQuadrant)
                            results.add(it)
                    }
                    if (results.size == 1)
                        res = results.first()
                }
            }
        }
        return res
    }
}