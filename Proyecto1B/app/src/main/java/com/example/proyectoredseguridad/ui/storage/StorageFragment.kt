package com.example.proyectoredseguridad.ui.storage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.proyectoredseguridad.R
import com.example.proyectoredseguridad.databinding.FragmentStorageBinding
import kotlinx.coroutines.launch

class StorageFragment : Fragment() {

    private var _binding: FragmentStorageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StorageViewModel by viewModels()

    private var selectedMechanism = StorageMechanism.SHARED_PREFS

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStorageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Selección inicial
        binding.toggleGroup.check(R.id.btnSharedPrefs)

        binding.toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                selectedMechanism = when (checkedId) {
                    R.id.btnSharedPrefs -> StorageMechanism.SHARED_PREFS
                    R.id.btnDataStore -> StorageMechanism.DATA_STORE
                    R.id.btnEncrypted -> StorageMechanism.ENCRYPTED_PREFS
                    else -> StorageMechanism.SHARED_PREFS
                }
            }
        }

        binding.btnSave.setOnClickListener {
            val key = binding.etKey.text.toString().trim()
            val value = binding.etValue.text.toString().trim()
            if (key.isEmpty()) {
                binding.tilKey.error = "La clave no puede estar vacía"
                return@setOnClickListener
            }
            if (value.isEmpty()) {
                binding.tilValue.error = "El valor no puede estar vacío"
                return@setOnClickListener
            }
            binding.tilKey.error = null
            binding.tilValue.error = null
            viewModel.save(selectedMechanism, key, value)
        }

        binding.btnRetrieve.setOnClickListener {
            val key = binding.etKey.text.toString().trim()
            if (key.isEmpty()) {
                binding.tilKey.error = "La clave no puede estar vacía"
                return@setOnClickListener
            }
            binding.tilKey.error = null
            viewModel.retrieve(selectedMechanism, key)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.result.collect { text ->
                        binding.tvResult.text = text
                    }
                }
                launch {
                    viewModel.isLoading.collect { loading ->
                        binding.progressBarStorage.isVisible = loading
                        binding.btnSave.isEnabled = !loading
                        binding.btnRetrieve.isEnabled = !loading
                        binding.toggleGroup.isEnabled = !loading
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
