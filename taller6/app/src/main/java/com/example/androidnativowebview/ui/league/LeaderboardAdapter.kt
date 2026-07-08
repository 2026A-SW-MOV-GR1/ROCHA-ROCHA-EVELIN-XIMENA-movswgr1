package com.example.androidnativowebview.ui.league

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androidnativowebview.R
import com.example.androidnativowebview.data.model.LeaderboardUser
import com.example.androidnativowebview.databinding.ItemLeaderboardBinding

class LeaderboardAdapter :
    ListAdapter<LeaderboardUser, LeaderboardAdapter.LeaderboardViewHolder>(LeaderboardDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val binding = ItemLeaderboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LeaderboardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class LeaderboardViewHolder(private val binding: ItemLeaderboardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: LeaderboardUser) {
            val ctx = binding.root.context

            // Medalla para el top 3, número para el resto
            binding.tvRank.text = when (user.rank) {
                1 -> "🥇"
                2 -> "🥈"
                3 -> "🥉"
                else -> "${user.rank}"
            }

            binding.tvUserInitials.text = user.initials
            binding.tvUserName.text = user.name
            binding.tvUserXp.text = "${user.xp} XP"
            binding.cvAvatar.setCardBackgroundColor(
                ContextCompat.getColor(ctx, user.avatarColorRes)
            )

            // Resaltar al usuario actual
            if (user.isCurrentUser) {
                binding.root.setBackgroundColor(ContextCompat.getColor(ctx, R.color.duo_green_light))
                binding.tvUserName.setTextColor(ContextCompat.getColor(ctx, R.color.duo_green_dark))
                binding.tvUserXp.setTextColor(ContextCompat.getColor(ctx, R.color.duo_green_dark))
            } else {
                binding.root.setBackgroundColor(ContextCompat.getColor(ctx, R.color.duo_white))
                binding.tvUserName.setTextColor(ContextCompat.getColor(ctx, R.color.duo_text_dark))
                binding.tvUserXp.setTextColor(ContextCompat.getColor(ctx, R.color.duo_green))
            }

            // Micro-animación: entrada con fade + slide desde la derecha
            binding.root.translationX = 60f
            binding.root.alpha = 0f
            binding.root.animate()
                .translationX(0f)
                .alpha(1f)
                .setDuration(250)
                .setStartDelay((bindingAdapterPosition * 40).toLong())
                .start()
        }
    }

    class LeaderboardDiffCallback : DiffUtil.ItemCallback<LeaderboardUser>() {
        override fun areItemsTheSame(old: LeaderboardUser, new: LeaderboardUser) =
            old.rank == new.rank
        override fun areContentsTheSame(old: LeaderboardUser, new: LeaderboardUser) =
            old == new
    }
}
