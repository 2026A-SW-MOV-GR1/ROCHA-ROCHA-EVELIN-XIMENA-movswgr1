package com.example.androidnativowebview.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidnativowebview.data.MockDataRepository
import com.example.androidnativowebview.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val lessonAdapter = LessonAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvLessons.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = lessonAdapter
            // Smooth scroll nativo — sin saltos de layout
            setHasFixedSize(false)
        }

        lessonAdapter.submitList(MockDataRepository.getLessons())

        // Fase C – Propuesta de mejora: actualizar barra de meta diaria
        updateDailyGoalProgress(current = 65, total = 100)
    }

    private fun updateDailyGoalProgress(current: Int, total: Int) {
        binding.progressDailyGoal.max = total
        binding.progressDailyGoal.progress = current
        binding.tvDailyGoalProgress.text = "$current / $total XP"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
