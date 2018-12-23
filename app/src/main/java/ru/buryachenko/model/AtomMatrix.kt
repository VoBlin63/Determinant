package ru.buryachenko.model

import ru.buryachenko.SIZE

class AtomMatrix {
    val matrix =  HashMap<Int, Atom>(SIZE*SIZE)

    init {
        for (row in 1..SIZE)
            for (col in 1..SIZE)
                matrix[makeId(row, col)] = Atom(row, col)
    }

    fun setNumber(id: Int, value: Int) {
        val element = matrix[id]
        element?.let {
            if (it.possible.contains(value)) {
                matrix[id]!!.number = value
                matrix.values.forEach { atom->
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
                    val allPossibleThisQuadrant = matrix.values.filter {it.quadrant == atom.quadrant && it != atom}.flatMap { it.possible }
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
