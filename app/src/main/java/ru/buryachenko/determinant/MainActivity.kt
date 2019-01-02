package ru.buryachenko.determinant

import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.support.constraint.ConstraintSet
import android.support.v4.content.res.ResourcesCompat
import android.view.View
import android.widget.Button
import android.widget.TextView
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
        var mainstay: TextView? = null
        for (row in 1..SIZE) {
            for (col in 1..SIZE) {
                val cell = TextView(this)
                cell.id = makeId(row, col)
                cell.text = viewModel.matrix[cell.id]!!.possibleStr
                setStylePossibleNumbers(cell)
                cell.setOnClickListener(this)
                cell.setOnLongClickListener {doLongClick(cell)}
                mainView.addView(cell)
                cell.setBackgroundResource(shapeByQadrant(cell.id))
                cell.width = resources.getDimensionPixelSize(R.dimen.cellWidth)
                cell.height = resources.getDimensionPixelSize(R.dimen.cellHeight)
                set.clone(mainView)
                set.clear(cell.id, ConstraintSet.TOP)
                set.clear(cell.id, ConstraintSet.LEFT)
                if (mainstay == null)
                    //первая ячейка в верхний левый
                    mainstay = cell
                else {
                    if (col == 1) {
                        //новая строчка начинается
                        set.connect(cell.id, ConstraintSet.TOP, makeId(row-1,1), ConstraintSet.BOTTOM, 0)
                    }
                    else {
                        set.connect(cell.id, ConstraintSet.TOP, mainstay.id, ConstraintSet.TOP, 0)
                        set.connect(cell.id, ConstraintSet.LEFT, mainstay.id, ConstraintSet.RIGHT, 0)
                    }
                }
                set.applyTo(mainView)
                mainstay = cell
            }
            val numSetButton = TextView(this)
            numSetButton.id = 10000 + row
            numSetButton.text = " $row"
            numSetButton.textSize = 31F
            numSetButton.setOnClickListener(this)
            numbersButton.add(numSetButton)
            mainView.addView(numSetButton)
            setNumberTextColor(numSetButton, true)
            numSetButton.setBackgroundResource(R.drawable.number_shape)
            numSetButton.width = resources.getDimensionPixelSize(R.dimen.cellWidth)
            numSetButton.height = resources.getDimensionPixelSize(R.dimen.cellHeight)
            set.clone(mainView)
            set.clear(numSetButton.id, ConstraintSet.TOP)
            set.clear(numSetButton.id, ConstraintSet.LEFT)
            set.connect(numSetButton.id, ConstraintSet.TOP, mainstay!!.id, ConstraintSet.TOP, 0)
            set.connect(numSetButton.id, ConstraintSet.LEFT, mainstay.id, ConstraintSet.RIGHT, resources.getDimensionPixelSize(R.dimen.cellNumberGap))
            set.applyTo(mainView)
        }
        resetButton.setOnClickListener { _ ->
            viewModel.resetAll()
            refreshMatrix()
            numbersButton.forEach {setNumberTextColor(it, true) }
        }

    }

    override fun onClick(v: View?) {
        if (v != null)
            doClick(v.id)
    }

    private fun doLongClick(v: View): Boolean {
        viewModel.setNumber(v.id,0)
        refreshMatrix()
        return true
    }

    private fun doClick(id: Int) {
        if (id < 10000) {
            if (id != clickedCellID) {
                if (clickedCellID > 0) {
                    val workItem = findViewById<TextView>(clickedCellID)
                    if (viewModel.matrix[clickedCellID]!!.number > 0)
                        setStyleDefinedNumber(workItem)
                    else
                        setStylePossibleNumbers(workItem)
                    workItem.setBackgroundResource(shapeByQadrant(clickedCellID))
                }
                clickedCellID = id
                viewModel.clearAllAuto()
                val clickedItem = findViewById<TextView>(clickedCellID)
                clickedItem.setBackgroundResource(R.drawable.cell_shape_cursor)
                val possibleNums = viewModel.matrix[id]!!.possible
                numbersButton.forEach {setNumberTextColor(it, possibleNums.contains(it.id - 10000))}
            }
        }
        else {
            if (clickedCellID > 0) {
                viewModel.setNumber(clickedCellID, id-10000)
                val possibleNums = viewModel.matrix[clickedCellID]!!.possible
                numbersButton.forEach {setNumberTextColor(it, possibleNums.contains(it.id - 10000))}
                refreshMatrix()
            }
        }
    }


    private fun refreshMatrix() {
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
        if (clickedCellID > 0) {
            val clickedItem = findViewById<TextView>(clickedCellID)
            clickedItem.setBackgroundResource(R.drawable.cell_shape_cursor)
        }
        if (needRefresh)
            refreshMatrix()
        if (clickedCellID > 0) {
            val possibleNums = viewModel.matrix[clickedCellID]!!.possible
            numbersButton.forEach { setNumberTextColor(it, possibleNums.contains(it.id - 10000)) }
        }
    }

    private fun setNumberTextColor(view: TextView, isPossible: Boolean) {
        view.setTextColor(ResourcesCompat.getColor(resources, if (isPossible) R.color.colorNumberTextPossible else R.color.colorNumberTextUnPossible, null))
    }

    private fun setStylePossibleNumbers(view: TextView) {
        view.textSize = 14F
        view.letterSpacing = 0.11F
        view.typeface = Typeface.MONOSPACE
        view.setTextColor(ResourcesCompat.getColor(resources, R.color.colorPossibleNumbers, null))
        view.setBackgroundResource(shapeByQadrant(view.id))
    }

    private fun setStyleDefinedNumber(view: TextView) {
        view.textSize = 39F
        view.typeface = Typeface.MONOSPACE
        view.setTextColor(ResourcesCompat.getColor(resources, R.color.colorDefinedNumber, null))
        view.setBackgroundResource(shapeByQadrant(view.id))
    }

    private fun shapeByQadrant(id: Int) = if( viewModel.matrix[id]!!.quadrant % 2 == 0) R.drawable.cell_shape else R.drawable.cell_shape_cross
}
