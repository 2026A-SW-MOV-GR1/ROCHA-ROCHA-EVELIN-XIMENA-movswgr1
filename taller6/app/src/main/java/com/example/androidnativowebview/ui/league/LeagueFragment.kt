package com.example.androidnativowebview.ui.league

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidnativowebview.data.MockDataRepository
import com.example.androidnativowebview.databinding.FragmentLeagueBinding

class LeagueFragment : Fragment() {

    private var _binding: FragmentLeagueBinding? = null
    private val binding get() = _binding!!
    private val leaderboardAdapter = LeaderboardAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeagueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvLeaderboard.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = leaderboardAdapter
            addItemDecoration(
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            )
            setHasFixedSize(true)
        }

        leaderboardAdapter.submitList(MockDataRepository.getLeaderboard())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
