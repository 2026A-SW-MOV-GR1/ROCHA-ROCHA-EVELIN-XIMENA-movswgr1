package com.example.duolingoclone.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.duolingoclone.R
import com.example.duolingoclone.data.model.Achievement
import com.example.duolingoclone.databinding.ItemAchievementBinding

class AchievementAdapter :
    ListAdapter<Achievement, AchievementAdapter.AchievementViewHolder>(AchievementDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val binding = ItemAchievementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AchievementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AchievementViewHolder(private val binding: ItemAchievementBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(achievement: Achievement) {
            val ctx = binding.root.context

            binding.tvAchievementEmoji.text = achievement.emoji
            binding.tvAchievementTitle.text = achievement.title
            binding.tvAchievementDesc.text = achievement.description

            if (achievement.isUnlocked) {
                binding.tvAchievementEmoji.alpha = 1f
                binding.cvAchievement.setCardBackgroundColor(
                    ContextCompat.getColor(ctx, R.color.duo_white)
                )
                binding.tvAchievementTitle.setTextColor(
                    ContextCompat.getColor(ctx, R.color.duo_text_dark)
                )
                binding.progressAchievement.visibility = View.GONE
                binding.ivAchievementLock.visibility = View.GONE
            } else {
                binding.tvAchievementEmoji.alpha = 0.3f
                binding.cvAchievement.setCardBackgroundColor(
                    ContextCompat.getColor(ctx, R.color.duo_bg)
                )
                binding.tvAchievementTitle.setTextColor(
                    ContextCompat.getColor(ctx, R.color.duo_text_gray)
                )
                binding.ivAchievementLock.visibility = View.VISIBLE

                if (achievement.progress > 0) {
                    binding.progressAchievement.visibility = View.VISIBLE
                    binding.progressAchievement.max = achievement.total
                    binding.progressAchievement.progress = achievement.progress
                } else {
                    binding.progressAchievement.visibility = View.GONE
                }
            }

            // Micro-animación: fade-in escalonado al aparecer
            binding.cvAchievement.alpha = 0f
            binding.cvAchievement.animate()
                .alpha(1f)
                .setDuration(300)
                .setStartDelay((bindingAdapterPosition * 50).toLong())
                .start()
        }
    }

    class AchievementDiffCallback : DiffUtil.ItemCallback<Achievement>() {
        override fun areItemsTheSame(old: Achievement, new: Achievement) = old.id == new.id
        override fun areContentsTheSame(old: Achievement, new: Achievement) = old == new
    }
}
