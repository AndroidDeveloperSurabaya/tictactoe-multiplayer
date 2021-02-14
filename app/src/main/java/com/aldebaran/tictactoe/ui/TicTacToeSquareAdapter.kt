package com.aldebaran.tictactoe.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aldebaran.tictactoe.R
import com.aldebaran.tictactoe.databinding.AdapterTictactoeSquareBinding
import com.aldebaran.tictactoe.model.TicTacToeSquare

class TicTacToeSquareAdapter (
    private val onSquareClick: (Int, TicTacToeSquare) -> Unit
) : RecyclerView.Adapter<TicTacToeSquareAdapter.ViewHolder>(){

    var squares = mutableListOf<TicTacToeSquare>()
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val bind = AdapterTictactoeSquareBinding
                .inflate(inflater, parent, false)

        return ViewHolder(bind)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val square = squares[position]
        holder.bindView(position, square)
    }

    fun setSquare(squares: List<TicTacToeSquare>) {
        this.squares.addAll(squares)
        notifyDataSetChanged()
    }

    fun updateSquare(position: Int, square: TicTacToeSquare) {
        squares.set(position, square)
        notifyItemChanged(position)
    }

    fun updateSquare(position: Int, playerId: Int) {
        squares[position].playerId = playerId
        squares[position].isPicked = true
        notifyItemChanged(position)
    }

    override fun getItemCount(): Int {
        return squares.size
    }

    inner class ViewHolder(private val binding: AdapterTictactoeSquareBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(position: Int, square: TicTacToeSquare) {
            binding.image.background = when (square.playerId) {
                1 -> ContextCompat.getDrawable(binding.image.context, R.drawable.ic_cross_blue)

                2 -> ContextCompat.getDrawable(binding.image.context, R.drawable.ic_circle_red)

                else -> ContextCompat.getDrawable(binding.image.context, android.R.color.white)
            }

            binding.container.setOnClickListener {
                if (!square.isPicked) {
                    onSquareClick.invoke(position, square)
                }
            }
        }
    }
}