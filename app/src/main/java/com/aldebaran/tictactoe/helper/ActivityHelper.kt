package com.aldebaran.tictactoe.helper

import androidx.appcompat.app.AppCompatActivity
import com.aldebaran.tictactoe.MyApp

fun AppCompatActivity.myApp() : MyApp {
    return (application as MyApp)
}