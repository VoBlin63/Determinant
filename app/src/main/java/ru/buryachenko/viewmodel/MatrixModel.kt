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
            if (it.possible.contains(value) || value == 0) {
                matrix.values.forEach { atom -> atom.bannedFrom.remove(id)}
                element.number = value
                element.auto = auto
                if (value > 0)
                    matrix.values.forEach { atom ->
                        if ((atom.row == element.row) || (atom.col == element.col) || (atom.quadrant == element.quadrant))
                            atom.bannedFrom[id] = value
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