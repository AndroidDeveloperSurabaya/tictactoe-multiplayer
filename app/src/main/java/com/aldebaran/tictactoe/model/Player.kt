package com.aldebaran.tictactoe.model

data class Player(
    val id: Int? = null,
    @JvmField var isReady: Boolean? = null,
    var name: String? = null
)