package ru.buryachenko.determinant

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.Constraints
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import ru.buryachenko.model.AtomMatrix
import ru.buryachenko.viewmodel.DeterminantViewModel


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModel = ViewModelProviders.of(this).get(DeterminantViewModel::class.java)

        val mainview = findViewById<ConstraintLayout>(R.id.main)

//        val wrapContent = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        val lParams = LinearLayout.LayoutParams(
//            wrapContent, wrapContent
//        )

        val cParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)

        cParams.startToStart = ConstraintLayout.LayoutParams.START
        val btn = Button(this)
        btn.text = "TEST"
        mainview.addView(btn)

        viewModel.field.printOut()
    }
}
