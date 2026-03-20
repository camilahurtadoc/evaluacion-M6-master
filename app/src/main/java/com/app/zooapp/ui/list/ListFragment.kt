package com.app.zooapp.ui.list

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.zooapp.data.repository.ZooRepository
import com.app.zooapp.databinding.FragmentListBinding

/**
 * Fragmento que muestra la lista de animales.
 * Utiliza un RecyclerView con un adaptador personalizado y SwipeRefreshLayout para recargar.
 */
class ListFragment: Fragment() {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ListViewModel
    private lateinit var adapter: ListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
            _binding = FragmentListBinding.inflate(inflater, container, false)
            return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inicializar ViewModel con su factory
        val repository = ZooRepository(requireContext())
        val factory = ListViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ListViewModel::class.java]

        setupRecyclerView()
        setupObservers()
        setupSwipeRefresh()
        //Carga inicial
        viewModel.refresh()
    }

    private fun setupRecyclerView() {
        adapter = ListAdapter { animal ->
            // Navega al detalle pasando el id del animal
            val action = ListFragmentDirections.actionListFragmentToDetailFragment(animal.id)
            findNavController().navigate(action)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        // Cada vez que la lista de animales cambia, se actualiza el RecyclerView
        viewModel.animals.observe(viewLifecycleOwner) { animals ->
            adapter.submitList(animals)
        }
        // Controla el indicador de carga del SwipeRefreshLayout
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
        }
        // Muestra errores al usuario mediante Toast
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null)
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class ListViewModelFactory(private val repository: ZooRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ListViewModel(repository) as T
        }
        throw IllegalArgumentException("clase ViewModel no reconocida")
    }
}