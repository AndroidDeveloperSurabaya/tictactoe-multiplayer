package com.aldebaran.tictactoe.model

data class TicTacToeSquare (
        val number: Int,
        var playerId: Int = 0,
        var isPicked: Boolean = false
)