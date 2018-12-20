package ru.buryachenko.determinant

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ru.buryachenko.model.AtomMatrix

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val model = AtomMatrix()
        model.printOut()
    }
}
