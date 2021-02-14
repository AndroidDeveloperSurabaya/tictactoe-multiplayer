package com.aldebaran.tictactoe.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.aldebaran.tictactoe.databinding.ActivitySelectPlayerBinding
import com.aldebaran.tictactoe.helper.AppConstant
import com.aldebaran.tictactoe.helper.AppConstant.PLAYER_ONE_ID
import com.aldebaran.tictactoe.helper.AppConstant.PLAYER_TWO_ID
import com.aldebaran.tictactoe.helper.myApp
import com.aldebaran.tictactoe.model.Player
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SelectPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectPlayerBinding
    private lateinit var playerListener: ValueEventListener

    private val playersRef = Firebase.database.reference.child(AppConstant.PLAYERS_FIELD)
    private val firstPlayerRef = playersRef.child(AppConstant.FIRST_PLAYER)
    private val secondPlayerRef = playersRef.child(AppConstant.SECOND_PLAYER)

    private var players = emptyList<Player>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Select Player"
        readPlayerStatus()
        viewClick()
    }

    override fun onDestroy() {
        playersRef.removeEventListener(playerListener)
        super.onDestroy()
    }

    private fun viewClick() {
        binding.btnReadyFirstPlayer.setOnClickListener {
            pickPlayer(AppConstant.FIRST_PLAYER)
        }

        binding.btnReadySecondPlayer.setOnClickListener {
            pickPlayer(AppConstant.SECOND_PLAYER)
        }

        binding.btnCancelFirstPlayer.setOnClickListener {
            cancelPickPlayer(AppConstant.FIRST_PLAYER)
        }

        binding.btnCancelSecondPlayer.setOnClickListener {
            cancelPickPlayer(AppConstant.SECOND_PLAYER)
        }
    }

    private fun readPlayerStatus() {
        playerListener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var isAllPlayerReady = true

                players = snapshot.children.map { child ->
                    val player = child.getValue(Player::class.java) ?: Player()
                    val isPlayerReady = player.isReady ?: false

                    if (!isPlayerReady) {
                        isAllPlayerReady = isPlayerReady
                    }

                    playerStatus(child.key.orEmpty(), player) // update UI

                    player
                }

                if (isAllPlayerReady) {
                    val intent = Intent(this@SelectPlayerActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("err", error.message)
            }
        }

        playersRef.addValueEventListener(playerListener)
    }

    private fun playerStatus(playerSelection: String, player: Player) {
        when (playerSelection) {
            AppConstant.FIRST_PLAYER -> {
                binding.textFirstPlayer.setText(player.name)
                binding.textFirstPlayer.isEnabled = !(player.isReady ?: false) && myApp().player == null
                binding.btnReadyFirstPlayer.isEnabled = !(player.isReady ?: false) && myApp().player == null
            }

            AppConstant.SECOND_PLAYER -> {
                binding.textSecondPlayer.setText(player.name)
                binding.textSecondPlayer.isEnabled = !(player.isReady ?: false) && myApp().player == null
                binding.btnReadySecondPlayer.isEnabled = !(player.isReady ?: false) && myApp().player == null
            }
        }
    }

    private fun pickPlayer(playerSelection: String) {
        binding.btnReadyFirstPlayer.isEnabled = false
        binding.btnReadySecondPlayer.isEnabled = false
        binding.textFirstPlayer.isEnabled = false
        binding.textSecondPlayer.isEnabled = false

        when (playerSelection) {
            AppConstant.FIRST_PLAYER -> {
                binding.btnCancelFirstPlayer.visibility = View.VISIBLE

                players.find { it.id == PLAYER_ONE_ID }?.also { player ->
                    player.name = "${binding.textFirstPlayer.text}"
                    player.isReady = true
                    firstPlayerRef.setValue(player)
                    myApp().player = player
                }
            }

            AppConstant.SECOND_PLAYER -> {
                binding.btnCancelSecondPlayer.visibility = View.VISIBLE

                players.find { it.id == PLAYER_TWO_ID }?.also { player ->
                    player.name = "${binding.textSecondPlayer.text}"
                    player.isReady = true
                    secondPlayerRef.setValue(player)
                    myApp().player = player
                }
            }
        }
    }

    private fun cancelPickPlayer(playerSelection: String) {
        myApp().player?.also { player ->

            player.isReady = false

            when (playerSelection) {
                AppConstant.FIRST_PLAYER -> {
                    player.name = "Player 1"
                    firstPlayerRef.setValue(player)

                    binding.btnCancelFirstPlayer.visibility = View.GONE
                }

                AppConstant.SECOND_PLAYER -> {
                    player.name = "Player 2"
                    secondPlayerRef.setValue(player)

                    binding.btnCancelSecondPlayer.visibility = View.GONE
                }
            }

            binding.btnReadyFirstPlayer.isEnabled = true
            binding.btnReadySecondPlayer.isEnabled = true
            binding.textFirstPlayer.isEnabled = true
            binding.textSecondPlayer.isEnabled = true
        }

        myApp().player = null
    }
}