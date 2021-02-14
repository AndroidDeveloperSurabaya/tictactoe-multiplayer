package com.aldebaran.tictactoe.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.aldebaran.tictactoe.databinding.ActivityMainBinding
import com.aldebaran.tictactoe.helper.AppConstant
import com.aldebaran.tictactoe.helper.AppConstant.PLAYER_ONE_ID
import com.aldebaran.tictactoe.helper.AppConstant.PLAYER_TWO_ID
import com.aldebaran.tictactoe.helper.GameHelper
import com.aldebaran.tictactoe.helper.GameStatus
import com.aldebaran.tictactoe.helper.myApp
import com.aldebaran.tictactoe.model.Player
import com.aldebaran.tictactoe.model.PlayerTurn
import com.aldebaran.tictactoe.model.TicTacToeSquare
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val adapter = TicTacToeSquareAdapter(this::onSquareClicked)
    private val gameRef = Firebase.database.reference.child(AppConstant.GAME)

    // player
    private val playersRef = Firebase.database.reference.child(AppConstant.PLAYERS_FIELD)
    private val firstPlayerRef = playersRef.child(AppConstant.FIRST_PLAYER)
    private val secondPlayerRef = playersRef.child(AppConstant.SECOND_PLAYER)

    // game
    private val playerTurnRef = gameRef.child(AppConstant.PLAYER_TURN)
    private val gameFirstPlayerRef = gameRef.child(AppConstant.FIRST_PLAYER)
    private val gameSecondPlayerRef = gameRef.child(AppConstant.SECOND_PLAYER)

    private var isDialogWinnerShowing = false

    private lateinit var playerTurnListener: ValueEventListener

    private lateinit var otherPlayerSquarePickedListener: ValueEventListener

    private var playerTurnId = PLAYER_ONE_ID


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSquare()

        readPlayerTurnStatus()
        readOtherPickedSquare()

        supportActionBar?.title = "You : Player ${myApp().player?.id}"
    }

    override fun onDestroy() {
        playerTurnRef.removeEventListener(playerTurnListener)
        super.onDestroy()
    }

    private fun onSquareClicked(position: Int, square: TicTacToeSquare) {
        myApp().player?.let { player ->

            if (playerTurnId == player.id) {
                pushSquarePicked(square)

                square.playerId = player.id
                square.isPicked = true
                adapter.updateSquare(position, square)

                checkPlayerWinner()
            }
        }
    }

    private fun setupSquare() {
        val squares = GameHelper.generateSquare()

        binding.squares.layoutManager = GridLayoutManager(this, 3)
        binding.squares.adapter = adapter
        adapter.setSquare(squares)
    }



    private fun readPlayerTurnStatus() {
        playerTurnListener = object: ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                val playerTurn = snapshot.getValue(PlayerTurn::class.java)
                playerTurn?.playerId?.let {
                    playerTurnId = it
                    binding.textPlayerTurn.text = "Player $playerTurnId Turn"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("err", error.message)
            }
        }

        playerTurnRef.addValueEventListener(playerTurnListener)
    }

    private fun readOtherPickedSquare() {
        myApp().player?.let { player ->

            otherPlayerSquarePickedListener = object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { child ->
                        val position = child.getValue(Int::class.java)
                        position?.let {
                            if (player.id == PLAYER_ONE_ID) {
                                adapter.updateSquare(it, PLAYER_TWO_ID)
                            } else {
                                adapter.updateSquare(it, PLAYER_ONE_ID)
                            }
                        }
                    }

                    checkPlayerWinner()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("err", error.message)
                }
            }

            if (player.id == 1) {
                gameSecondPlayerRef.addValueEventListener(otherPlayerSquarePickedListener)
            } else {
                gameFirstPlayerRef.addValueEventListener(otherPlayerSquarePickedListener)
            }
        }
    }

    private fun pushSquarePicked(square: TicTacToeSquare) {
        myApp().player?.let { player ->
            if (player.id == 1) {
                gameFirstPlayerRef.push().setValue(square.number)
                playerTurnRef.setValue(PlayerTurn(PLAYER_TWO_ID))
            } else {
                gameSecondPlayerRef.push().setValue(square.number)
                playerTurnRef.setValue(PlayerTurn(PLAYER_ONE_ID))
            }
        }
    }

    private fun checkPlayerWinner() {
        myApp().player?.also { player ->

            val winner = GameHelper.checkWinner(adapter.squares)

            if (winner > 0) {
                if (winner == player.id) {
                    showDialogWinner("You win")
                } else if (winner == GameStatus.GAME_DRAW) {
                    showDialogWinner("Draw")
                } else {
                    val otherPlayer = if (player.id == PLAYER_ONE_ID) PLAYER_TWO_ID else PLAYER_ONE_ID
                    showDialogWinner("Player $otherPlayer")
                }
            }
        }
    }

    private fun showDialogWinner(message: String) {
        if (!isDialogWinnerShowing) {
            val dialog = AlertDialog.Builder(this)
                .setTitle("Game Ended")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { dialog, _ ->
                    resetGame()

                    val intent = Intent(this, SelectPlayerActivity::class.java)
                    startActivity(intent)

                    dialog.dismiss()
                    finish()
                }
            dialog.show().also {
                isDialogWinnerShowing = it.isShowing
            }
        }
    }

    private fun resetGame() {
        gameFirstPlayerRef.removeValue()
        gameSecondPlayerRef.removeValue()
        playerTurnRef.setValue(PlayerTurn(PLAYER_ONE_ID))

        firstPlayerRef.setValue(Player(PLAYER_ONE_ID,false, "Player 1"))
        secondPlayerRef.setValue(Player(PLAYER_TWO_ID,false, "Player 2"))

        myApp().player = null
    }
}