package ru.buryachenko.determinant

import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.support.constraint.ConstraintSet
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import ru.buryachenko.SIZE
import ru.buryachenko.model.makeId
import ru.buryachenko.viewmodel.MatrixModel
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var viewModel: MatrixModel
    private var clickedCellID = 0
    private val numbersButton = ArrayList<TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(MatrixModel::class.java)

        val mainView = findViewById<ConstraintLayout>(R.id.main)
        val resetButton = findViewById<Button>(R.id.reset)

        val set = ConstraintSet()
        for (row in 1..SIZE) {
            var benchMark = ConstraintSet.PARENT_ID
            for (col in 1..SIZE) {
                val picture = TextView(this)
                picture.id = makeId(row, col)

                picture.width = resources.getDimensionPixelSize(R.dimen.cellWidth)
                picture.height = resources.getDimensionPixelSize(R.dimen.cellHeight)
                picture.text = viewModel.matrix[picture.id]!!.possibleStr
                setStylePossibleNumbers(picture)
                picture.setOnClickListener(this)
                picture.setBackgroundColor(colorBackgroundByQadrant(picture.id))
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
            numSetButton.text = " $row"
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
        resetButton.setOnClickListener {
            viewModel.resetAll()
            refreshMatrix()
            numbersButton.forEach {it.setTextColor(Color.BLACK)}
        }

    }

    override fun onClick(v: View?) {
        if (v != null) {
            doClick(v.id)
        }
    }

    private fun doClick(id: Int) {
        if (id < 10000) {
            if (id != clickedCellID) {
                if (clickedCellID > 0) {
                    if (viewModel.matrix[clickedCellID]!!.number > 0)
                        setStyleDefinedNumber(findViewById(clickedCellID))
                    else
                        setStylePossibleNumbers(findViewById(clickedCellID))
                }
                clickedCellID = id
                viewModel.clearAllAuto()
                findViewById<TextView>(clickedCellID).setBackgroundColor(Color.GREEN)

                val possibleNums = viewModel.matrix[id]!!.possible
                numbersButton.forEach {it.setTextColor(if (possibleNums.contains(it.id - 10000)) Color.BLACK else Color.GRAY)}
            }
        }
        else {
            if (clickedCellID > 0) {
                viewModel.setNumber(clickedCellID, id-10000)
                val possibleNums = viewModel.matrix[clickedCellID]!!.possible
                numbersButton.forEach {it.setTextColor(if (possibleNums.contains(it.id - 10000)) Color.BLACK else Color.GRAY)}
                refreshMatrix()
            }
        }
    }


    fun refreshMatrix() {
        var needRefresh = false
        viewModel.matrix.forEach{
            val item = findViewById<TextView>(it.key)
            if (it.value.number == 0) {
                setStylePossibleNumbers(item)
                item.text = it.value.possibleStr
                val defined = viewModel.defineValue(it.value)
                if (defined > 0) {
                    viewModel.setNumber(it.key, defined, true)
                    needRefresh = true
                }
            }
            else {
                item.text = it.value.number.toString()
                setStyleDefinedNumber(item)
                if (it.value.auto)
                    item.setTextColor(Color.RED)
            }
        }
        findViewById<TextView>(clickedCellID).setBackgroundColor(Color.GREEN)
        if (needRefresh)
            refreshMatrix()
    }

    private fun setStylePossibleNumbers(view: TextView) {
        view.textSize = 15F
        view.letterSpacing = 0.11F
        view.typeface = Typeface.MONOSPACE
        view.setLines(3)
        view.setTextColor(resources.getColor(R.color.colorPossibleNumbers))
        view.setBackgroundColor(colorBackgroundByQadrant(view.id))
    }

    private fun setStyleDefinedNumber(view: TextView) {
        view.textSize = 39F
        view.typeface = Typeface.MONOSPACE
        view.setLines(1)
        view.setTextColor(resources.getColor(R.color.colorDefinedNumber))
        view.setBackgroundColor(colorBackgroundByQadrant(view.id))
    }

    fun colorBackgroundByQadrant(id: Int) = if( viewModel.matrix[id]!!.quadrant % 2 == 0) Color.WHITE else resources.getColor(R.color.colorBackgroundSecond)

}
