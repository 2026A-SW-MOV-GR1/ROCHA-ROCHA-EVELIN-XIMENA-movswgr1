package com.example.proyectoredseguridad.ui.storage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.proyectoredseguridad.R
import com.example.proyectoredseguridad.databinding.FragmentSecretsBinding

class SecretsFragment : Fragment() {

    private var _binding: FragmentSecretsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SecretsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecretsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()

        binding.btnGuardar.setOnClickListener {
            val llave = binding.etLlave.text.toString().trim()
            val valor = binding.etValor.text.toString().trim()
            if (llave.isEmpty()) {
                binding.tilLlave.error = "La llave no puede estar vacía"
                return@setOnClickListener
            }
            if (valor.isEmpty()) {
                binding.tilValor.error = "El valor no puede estar vacío"
                return@setOnClickListener
            }
            binding.tilLlave.error = null
            binding.tilValor.error = null
            viewModel.guardar(getMecanismoSeleccionado(), llave, valor)
        }

        binding.btnRecuperar.setOnClickListener {
            val llave = binding.etLlave.text.toString().trim()
            if (llave.isEmpty()) {
                binding.tilLlave.error = "La llave no puede estar vacía"
                return@setOnClickListener
            }
            binding.tilLlave.error = null
            viewModel.recuperar(getMecanismoSeleccionado(), llave)
        }
    }

    private fun getMecanismoSeleccionado(): Mecanismo {
        return when (binding.radioGroup.checkedRadioButtonId) {
            R.id.rbDataStore -> Mecanismo.DATA_STORE
            R.id.rbEncrypted -> Mecanismo.ENCRYPTED_SHARED_PREFERENCES
            else -> Mecanismo.SHARED_PREFERENCES
        }
    }

    private fun observeViewModel() {
        viewModel.resultado.observe(viewLifecycleOwner) { texto ->
            binding.tvResultado.text = texto
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.isVisible = loading
            binding.btnGuardar.isEnabled = !loading
            binding.btnRecuperar.isEnabled = !loading
            binding.radioGroup.isEnabled = !loading
            binding.rbSharedPrefs.isEnabled = !loading
            binding.rbDataStore.isEnabled = !loading
            binding.rbEncrypted.isEnabled = !loading
            binding.etLlave.isEnabled = !loading
            binding.etValor.isEnabled = !loading
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
