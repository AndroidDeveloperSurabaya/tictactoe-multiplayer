package com.aldebaran.tictactoe.helper

import com.aldebaran.tictactoe.model.TicTacToeSquare

object GameHelper {

    fun generateSquare(): List<TicTacToeSquare> {
        val squares = mutableListOf<TicTacToeSquare>()
        for (num in 0 until 9) {
            val square = TicTacToeSquare(num)
            squares.add(square)
        }

        return squares
    }

    fun checkWinner(squares: List<TicTacToeSquare>): Int {
        val firstPlayerSquare = mutableListOf<Int>()
        val secondPlayerSquare = mutableListOf<Int>()

        squares.forEach { square ->
            if (square.playerId == 1) {
                firstPlayerSquare.add(square.number)
            } else if (square.playerId == 2) {
                secondPlayerSquare.add(square.number)
            }
        }

        var winner = GameStatus.GAME_STILL_RUNNING

        //row1
        if (firstPlayerSquare.contains(0) && firstPlayerSquare.contains(1) && firstPlayerSquare.contains(2)) {
            winner = GameStatus.PLAYER_ONE_WIN
        }

        else if (secondPlayerSquare.contains(0) && secondPlayerSquare.contains(1) && secondPlayerSquare.contains(2)) {
            winner = GameStatus.PLAYER_TWO_WIN
        }

        //row2
        else if (firstPlayerSquare.contains(3) && firstPlayerSquare.contains(4) && firstPlayerSquare.contains(5)) {
            winner = GameStatus.PLAYER_ONE_WIN
        }

        else if (secondPlayerSquare.contains(3) && secondPlayerSquare.contains(4) && secondPlayerSquare.contains(5)) {
            winner = GameStatus.PLAYER_TWO_WIN
        }

        //row3
        else if (firstPlayerSquare.contains(6) && firstPlayerSquare.contains(7) && firstPlayerSquare.contains(8)) {
            winner = GameStatus.PLAYER_ONE_WIN
        }

        else if (secondPlayerSquare.contains(6) && secondPlayerSquare.contains(7) && secondPlayerSquare.contains(8)) {
            winner = GameStatus.PLAYER_TWO_WIN
        }

        //col1
        else if (firstPlayerSquare.contains(0) && firstPlayerSquare.contains(3) && firstPlayerSquare.contains(6)) {
            winner = GameStatus.PLAYER_ONE_WIN
        }

        else if (secondPlayerSquare.contains(0) && secondPlayerSquare.contains(3) && secondPlayerSquare.contains(6)) {
            winner = GameStatus.PLAYER_TWO_WIN
        }

        //col2
        else if (firstPlayerSquare.contains(1) && firstPlayerSquare.contains(4) && firstPlayerSquare.contains(7)) {
            winner = GameStatus.PLAYER_ONE_WIN
        }

        else if (secondPlayerSquare.contains(1) && secondPlayerSquare.contains(4) && secondPlayerSquare.contains(7)) {
            winner = GameStatus.PLAYER_TWO_WIN
        }

        //col3
        else if (firstPlayerSquare.contains(2) && firstPlayerSquare.contains(5) && firstPlayerSquare.contains(9)) {
            winner = GameStatus.PLAYER_ONE_WIN
        }

        else if (secondPlayerSquare.contains(2) && secondPlayerSquare.contains(5) && secondPlayerSquare.contains(9)) {
            winner = GameStatus.PLAYER_TWO_WIN
        }

        //cross1
        else if (firstPlayerSquare.contains(0) && firstPlayerSquare.contains(4) && firstPlayerSquare.contains(8)) {
            winner = GameStatus.PLAYER_ONE_WIN
        }

        else if (secondPlayerSquare.contains(0) && secondPlayerSquare.contains(4) && secondPlayerSquare.contains(8)) {
            winner = GameStatus.PLAYER_TWO_WIN
        }

        //cross2
        else if (firstPlayerSquare.contains(2) && firstPlayerSquare.contains(4) && firstPlayerSquare.contains(6)) {
            winner = GameStatus.PLAYER_ONE_WIN
        }

        else if (secondPlayerSquare.contains(2) && secondPlayerSquare.contains(4) && secondPlayerSquare.contains(6)) {
            winner = GameStatus.PLAYER_TWO_WIN
        }

        //draw
        else if ((firstPlayerSquare.size + secondPlayerSquare.size) == 9 && winner == 0) {
            winner = GameStatus.GAME_DRAW
        }

        return winner
    }
}