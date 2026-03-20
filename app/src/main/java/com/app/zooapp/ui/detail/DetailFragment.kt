package com.app.zooapp.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.app.zooapp.R
import com.app.zooapp.data.db.entity.AnimalEntity
import com.app.zooapp.data.repository.ZooRepository
import com.app.zooapp.databinding.FragmentDetailBinding
import com.app.zooapp.utils.Constants
import com.bumptech.glide.Glide

/**
 * Fragmento que muestra el detalle completo de un animal.
 * Recibe el id del animal como argumento.
 */
class DetailFragment: Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DetailViewModel
    private var animalId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener el id del animal desde los argumentos de navegación
        animalId = arguments?.getInt("animalId") ?: -1
        if (animalId == -1) {
            Toast.makeText(requireContext(), "Error, animal no encontrado", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }
        val repository = ZooRepository(requireContext())
        val factory = DetailViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[DetailViewModel::class.java]

        setupObservers()
        viewModel.loadAnimal(animalId)
        binding.btnSendEmail.setOnClickListener{
            sendEmail()
        }
    }

    private fun setupObservers() {
        // Cuando el animal se carga (o actualiza), se actualiza la interfaz de usuario
        viewModel.animal.observe(viewLifecycleOwner) { animal ->
            if (animal != null)
                bindData(animal)
        }
        // Maneja el estado de carga (podría mostrarse un ProgressBar)
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        // Muestra errores si ocurren al cargar el detalle
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null)
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun bindData(animal: AnimalEntity) {
        // Asignando los valores a los TextView
        binding.tvNombre.text = animal.nombre
        binding.tvEspecie.text = animal.especie
        binding.tvHabitat.text = animal.habitat
        binding.tvDieta.text = animal.dieta
        binding.tvDescripcion.text = animal.descripcion ?: "Sin descripción"
        binding.tvEstadoConservacion.text = animal.estadoConservacion ?: "No disponible"
        binding.tvEsperanzaVida.text = animal.esperanzaVida ?: "No disponible"
        binding.tvPesoPromedio.text = animal.pesoPromedio ?: "No disponible"
        binding.tvAlturaPromedio.text = animal.alturaPromedio ?: "No disponible"
        binding.tvDatosCuriosos.text = animal.datosCuriosos?.joinToString(", ") ?: "No disponible"
        binding.tvComidasFavoritas.text = animal.comidasFavoritas?.joinToString(", ") ?: "No disponible"
        binding.tvPredadoresNaturales.text = animal.predadoresNaturales?.joinToString(", ") ?: "No disponible"

        //Carga de imagenes (Glide)
        Glide.with(this)
            .load(animal.imagen)
            .placeholder(R.drawable.ic_animal_placeholder)
            .into(binding.ivAnimalImage)
    }

    /**
     * Construye un intent para enviar un correo con la información del animal.
     * Utiliza el esquema mailto: con asunto y cuerpo predefinidos.
     */
    private fun sendEmail() {
        val animal = viewModel.animal.value ?: return
        val subject = String.format(Constants.EMAIL_SUBJECT, animal.nombre)
        val body = String.format(Constants.EMAIL_BODY, animal.nombre)
        // Codificar asunto y cuerpo para que sean válidos en una URI
        val encodedSubject = Uri.encode(subject)
        val encodedBody = Uri.encode(body)
        val uriString = "mailto:${Constants.EMAIL_RECIPIENT}?subject=$encodedSubject&body=$encodedBody"
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse(uriString))
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "No hay aplicación de correo disponible", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class DetailViewModelFactory(private val repository: ZooRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailViewModel(repository) as T
        }
        throw IllegalArgumentException("clase ViewModel no reconocida")
    }
}