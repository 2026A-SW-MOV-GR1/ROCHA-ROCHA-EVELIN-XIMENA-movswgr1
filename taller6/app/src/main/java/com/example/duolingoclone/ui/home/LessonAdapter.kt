package com.example.duolingoclone.ui.home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.duolingoclone.R
import com.example.duolingoclone.data.model.Lesson
import com.example.duolingoclone.databinding.ItemLessonBinding

class LessonAdapter : ListAdapter<Lesson, LessonAdapter.LessonViewHolder>(LessonDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val binding = ItemLessonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LessonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        holder.bind(getItem(position))
        // Ocultar conector en el último ítem
        holder.binding.viewConnector.visibility =
            if (position == itemCount - 1) View.INVISIBLE else View.VISIBLE
    }

    inner class LessonViewHolder(val binding: ItemLessonBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(lesson: Lesson) {
            binding.tvLessonEmoji.text = lesson.emoji
            binding.tvLessonTitle.text = lesson.title

            when {
                lesson.isCompleted -> applyCompletedState(lesson)
                !lesson.isLocked   -> applyActiveState(lesson)
                else               -> applyLockedState(lesson)
            }

            binding.cvLesson.setOnClickListener {
                if (!lesson.isLocked) animatePress(it)
            }
        }

        private fun applyCompletedState(lesson: Lesson) {
            val ctx = binding.root.context
            binding.cvLesson.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.duo_green_light))
            binding.tvLessonEmoji.alpha = 1f
            binding.tvLessonTitle.setTextColor(ContextCompat.getColor(ctx, R.color.duo_green_dark))
            binding.tvXpReward.text = "${lesson.xpReward} XP"
            binding.tvXpReward.visibility = View.VISIBLE
            binding.tvXpReward.setTextColor(ContextCompat.getColor(ctx, R.color.duo_green_dark))
            binding.ivCheckmark.visibility = View.VISIBLE
            binding.ivLock.visibility = View.GONE
            binding.progressLesson.visibility = View.GONE
        }

        private fun applyActiveState(lesson: Lesson) {
            val ctx = binding.root.context
            binding.cvLesson.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.duo_white))
            binding.tvLessonEmoji.alpha = 1f
            binding.tvLessonTitle.setTextColor(ContextCompat.getColor(ctx, R.color.duo_text_dark))
            binding.tvXpReward.text = "${lesson.xpReward} XP"
            binding.tvXpReward.visibility = View.VISIBLE
            binding.tvXpReward.setTextColor(ContextCompat.getColor(ctx, R.color.duo_green))
            binding.ivCheckmark.visibility = View.GONE
            binding.ivLock.visibility = View.GONE

            if (lesson.currentProgress > 0) {
                binding.progressLesson.visibility = View.VISIBLE
                binding.progressLesson.max = lesson.totalProgress
                binding.progressLesson.progress = lesson.currentProgress
            } else {
                binding.progressLesson.visibility = View.GONE
            }
            // Micro-animación: pulso suave en la lección activa
            pulseAnimation(binding.cvLesson)
        }

        private fun applyLockedState(lesson: Lesson) {
            val ctx = binding.root.context
            binding.cvLesson.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.duo_bg))
            binding.tvLessonEmoji.alpha = 0.35f
            binding.tvLessonTitle.setTextColor(ContextCompat.getColor(ctx, R.color.duo_text_gray))
            binding.tvXpReward.visibility = View.GONE
            binding.ivCheckmark.visibility = View.GONE
            binding.ivLock.visibility = View.VISIBLE
            binding.progressLesson.visibility = View.GONE
        }

        // Micro-animación: pulso sutil para indicar "aquí debes continuar"
        private fun pulseAnimation(view: View) {
            view.clearAnimation()
            val sx = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.04f, 1f)
            val sy = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.04f, 1f)
            AnimatorSet().apply {
                playTogether(sx, sy)
                duration = 1400
                interpolator = AccelerateDecelerateInterpolator()
                repeatCount = ObjectAnimator.INFINITE
                start()
            }
        }

        // Micro-animación: rebote al presionar
        private fun animatePress(view: View) {
            val down = AnimatorSet().apply {
                playTogether(
                    ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.93f),
                    ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.93f)
                )
                duration = 90
            }
            val up = AnimatorSet().apply {
                playTogether(
                    ObjectAnimator.ofFloat(view, "scaleX", 0.93f, 1f),
                    ObjectAnimator.ofFloat(view, "scaleY", 0.93f, 1f)
                )
                duration = 90
            }
            AnimatorSet().apply {
                playSequentially(down, up)
                start()
            }
        }
    }

    class LessonDiffCallback : DiffUtil.ItemCallback<Lesson>() {
        override fun areItemsTheSame(oldItem: Lesson, newItem: Lesson) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Lesson, newItem: Lesson) = oldItem == newItem
    }
}
