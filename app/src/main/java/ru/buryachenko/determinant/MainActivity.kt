package ru.buryachenko.determinant

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import ru.buryachenko.viewmodel.DeterminantViewModel
import android.support.constraint.ConstraintSet
import android.view.View
import android.widget.TextView
import android.widget.Toast
import ru.buryachenko.SIZE
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var viewModel: DeterminantViewModel
    private var clickedCellID = 0
    private val numbersButton = ArrayList<TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(DeterminantViewModel::class.java)

        val mainView = findViewById<ConstraintLayout>(R.id.main)

        val set = ConstraintSet()
        for (row in 1..SIZE) {
            var benchMark = ConstraintSet.PARENT_ID
            for (col in 1..SIZE) {
                val picture = TextView(this)
                picture.id = makeId(row, col)

                picture.width = resources.getDimensionPixelSize(R.dimen.cellWidth)
                picture.height = resources.getDimensionPixelSize(R.dimen.cellHeight)
                picture.text = viewModel.field.possibleStr(viewModel.field.getPossible(row, col))
                setStylePossibleNumbers(picture)
                picture.setOnClickListener(this)
                picture.setBackgroundColor(colorBackgroundByQadrant(row, col))
                //picture.setBackgroundColor(resources.getColor(if ((row + col) % 2 == 0) R.color.colorBackgroundFirst else R.color.colorBackgroundSecond))
                mainView.addView(picture)
                set.clone(mainView)
                set.clear(picture.id, ConstraintSet.TOP)
                set.clear(picture.id, ConstraintSet.LEFT)
                set.connect(picture.id, ConstraintSet.TOP, benchMark, ConstraintSet.TOP, 0)
                if (benchMark == ConstraintSet.PARENT_ID) {
                    set.connect(picture.id, ConstraintSet.TOP, benchMark, ConstraintSet.TOP, (row - 1) * resources.getDimensionPixelSize(R.dimen.cellHeight))
                }
                else {
                    set.connect(picture.id, ConstraintSet.TOP, benchMark, ConstraintSet.TOP, 0)
                    set.connect(picture.id, ConstraintSet.LEFT, benchMark, ConstraintSet.RIGHT, 0)
                }
                set.applyTo(mainView)
                benchMark = picture.id
            }
            val numSetButton = TextView(this)
            numSetButton.id = 10000 + row
            numSetButton.width = resources.getDimensionPixelSize(R.dimen.cellWidth)
            numSetButton.height = resources.getDimensionPixelSize(R.dimen.cellHeight)
            numSetButton.text = "$row"
            numSetButton.textSize = 31F
            numSetButton.setOnClickListener(this)
            numSetButton.setBackgroundColor(Color.GRAY)
            numbersButton.add(numSetButton)
            mainView.addView(numSetButton)
            set.clone(mainView)
            set.clear(numSetButton.id, ConstraintSet.TOP)
            set.clear(numSetButton.id, ConstraintSet.LEFT)
            set.connect(numSetButton.id, ConstraintSet.TOP, benchMark, ConstraintSet.TOP, 0)
            set.connect(numSetButton.id, ConstraintSet.TOP, benchMark, ConstraintSet.TOP, 0)
            set.connect(numSetButton.id, ConstraintSet.LEFT, benchMark, ConstraintSet.RIGHT, 0)
            set.applyTo(mainView)
        }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            doClick(v.id)
        }
    }

    private fun doClick(id: Int) {
        val col = defineCol(id)
        val row = defineRow(id)
        if (id < 10000) {
            if (id != clickedCellID) {
                if (clickedCellID > 0) {
                    if (viewModel.field.get(defineRow(clickedCellID), defineCol(clickedCellID)).number > 0)
                        setStyleDefinedNumber(findViewById<TextView>(clickedCellID))
                    else
                        setStylePossibleNumbers(findViewById<TextView>(clickedCellID))
                }
                clickedCellID = id
                findViewById<TextView>(clickedCellID).setBackgroundColor(Color.GREEN)
                val possibleNums = viewModel.field.getPossible(defineRow(clickedCellID), defineCol(clickedCellID)).map { "$it" }
                numbersButton.forEach {it.setTextColor(if (it.text in possibleNums) Color.BLACK else Color.GRAY)}
            }
        }
        else {
            if (clickedCellID > 0) {
                viewModel.field.setNumber(defineRow(clickedCellID), defineCol(clickedCellID), id-10000)
                val possibleNums = viewModel.field.getPossible(defineRow(clickedCellID), defineCol(clickedCellID)).map { "$it" }
                numbersButton.forEach {it.setTextColor(if (it.text in possibleNums) Color.BLACK else Color.GRAY)}

                refreshMatrix()
                //Toast.makeText(applicationContext,"Set ${id-10000}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun refreshMatrix() {
        viewModel.field.matrix.forEach{
            val item = findViewById<TextView>(makeId(it.row, it.col))
            if (it.number == 0) {
                val listPossible = viewModel.field.getPossible(it.row, it.col)
                item.text = viewModel.field.possibleStr(listPossible)
                setStylePossibleNumbers(item)
                if (listPossible.size == 1)
                    item.setTextColor(Color.RED)
            }
            else {
                item.text = it.number.toString()
                setStyleDefinedNumber(item)
            }
        }
    }

    private fun makeId(row: Int, col: Int) = 1000 + row * 100 + col
    private fun defineRow(id: Int) = (id - 1000) / 100
    private fun defineCol(id: Int) = (id - 1000) % 100

    private fun setStylePossibleNumbers(view: TextView) {
        view.textSize = 15F
        view.letterSpacing = 0.11F
        view.typeface = Typeface.MONOSPACE
        view.setLines(3)
        view.setTextColor(resources.getColor(R.color.colorPossibleNumbers))
        view.setBackgroundColor(colorBackgroundByQadrant(defineRow(view.id), defineCol(view.id)))
    }

    private fun setStyleDefinedNumber(view: TextView) {
        view.textSize = 37F
        view.typeface = Typeface.MONOSPACE
        view.setLines(1)
        view.setTextColor(resources.getColor(R.color.colorDefinedNumber))
        view.setBackgroundColor(colorBackgroundByQadrant(defineRow(view.id), defineCol(view.id)))
    }

    fun colorBackgroundByQadrant(row: Int, col: Int) = if( viewModel.field.quadrant(row, col) % 2 == 0) Color.WHITE else Color.LTGRAY
}
