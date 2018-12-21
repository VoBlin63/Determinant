package ru.buryachenko.viewmodel

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModel
import ru.buryachenko.Determinant
import ru.buryachenko.model.AtomMatrix

class DeterminantViewModel : ViewModel() {

    val field = AtomMatrix()

    fun init() {

    }
}

