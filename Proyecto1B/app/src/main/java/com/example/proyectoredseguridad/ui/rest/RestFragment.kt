package com.example.proyectoredseguridad.ui.rest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.proyectoredseguridad.databinding.FragmentRestBinding

class RestFragment : Fragment() {

    private var _binding: FragmentRestBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RestViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()

        binding.btnGet.setOnClickListener {
            val idText = binding.etPostId.text.toString().trim()
            if (idText.isEmpty()) {
                binding.tilPostId.error = "Ingresa un ID"
                return@setOnClickListener
            }
            val id = idText.toIntOrNull()
            if (id == null || id !in 1..100) {
                binding.tilPostId.error = "ID debe estar entre 1 y 100"
                return@setOnClickListener
            }
            binding.tilPostId.error = null
            viewModel.getPost(id)
        }

        binding.btnPut.setOnClickListener {
            val idText = binding.etPostId.text.toString().trim()
            val id = idText.toIntOrNull()
            if (id == null || id !in 1..100) {
                binding.tilPostId.error = "Ingresa un ID válido primero"
                return@setOnClickListener
            }
            val title = binding.etTitle.text.toString().trim()
            val body = binding.etBody.text.toString().trim()
            if (title.isEmpty()) {
                binding.tilTitle.error = "El título no puede estar vacío"
                return@setOnClickListener
            }
            if (body.isEmpty()) {
                binding.tilBody.error = "El cuerpo no puede estar vacío"
                return@setOnClickListener
            }
            binding.tilPostId.error = null
            binding.tilTitle.error = null
            binding.tilBody.error = null
            viewModel.updatePost(id, title, body)
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.isVisible = loading
            setInputsEnabled(!loading)
        }

        viewModel.post.observe(viewLifecycleOwner) { post ->
            if (post != null) {
                binding.tvPostTitle.text = "Título: ${post.title}"
                binding.tvPostBody.text = "Cuerpo: ${post.body}"
                binding.etTitle.setText(post.title)
                binding.etBody.setText(post.body)
            }
        }

        viewModel.statusMessage.observe(viewLifecycleOwner) { message ->
            binding.tvStatus.text = message
        }
    }

    private fun setInputsEnabled(enabled: Boolean) {
        binding.etPostId.isEnabled = enabled
        binding.btnGet.isEnabled = enabled
        binding.etTitle.isEnabled = enabled
        binding.etBody.isEnabled = enabled
        binding.btnPut.isEnabled = enabled
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
