package ru.buryachenko.determinant

import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import ru.buryachenko.viewmodel.DeterminantViewModel
import android.support.constraint.ConstraintSet
import android.util.Log
import android.widget.TextView
import ru.buryachenko.LOGTAG
import ru.buryachenko.SIZE


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModel = ViewModelProviders.of(this).get(DeterminantViewModel::class.java)

        val mainView = findViewById<ConstraintLayout>(R.id.main)

        val set = ConstraintSet()
        for (row in 1..SIZE) {
            var benchMark = ConstraintSet.PARENT_ID
            for (col in 1..SIZE) {
                val picture = TextView(this)
                picture.id = 100 + row * SIZE + col

                picture.width = resources.getDimensionPixelSize(R.dimen.cellWidth)
                picture.height = resources.getDimensionPixelSize(R.dimen.cellHeight)
                picture.textSize = 15F
                picture.letterSpacing = 0.11F
                picture.text = "123456789"
                picture.setLines(3)
                picture.setBackgroundColor(resources.getColor(if ((row + col) % 2 == 0) R.color.colorBackgroundFirst else R.color.colorBackgroundSecond))
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

        }


//        viewModel.field.printOut()
    }
}
