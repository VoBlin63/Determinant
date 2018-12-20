package ru.buryachenko.model

import ru.buryachenko.SIZE

class AtomMatrix {
    val matrix =  Array<Atom>(SIZE,) //ArrayList<Atom>(SIZE*SIZE)
    val possibleNumbers = ArrayList<MutableSet<Int>>(SIZE*SIZE)


    init {
        for (row in 1..9)
            for (col in 1..9)
                matrix.add(Atom(row, col))
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

    }

    private fun fullStackNumbers()= setOf(1..SIZE) as MutableSet<Int>
}