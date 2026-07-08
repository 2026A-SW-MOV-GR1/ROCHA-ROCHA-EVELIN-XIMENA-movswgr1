package com.example.androidnativowebview.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidnativowebview.data.MockDataRepository
import com.example.androidnativowebview.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val achievementAdapter = AchievementAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvAchievements.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = achievementAdapter
            // nestedScrollingEnabled="false" declarado en XML;
            // el scroll lo maneja el NestedScrollView padre.
            setHasFixedSize(false)
        }

        achievementAdapter.submitList(MockDataRepository.getAchievements())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
