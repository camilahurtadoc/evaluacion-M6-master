package com.app.zooapp.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.zooapp.data.db.entity.AnimalEntity
import com.app.zooapp.databinding.ItemAnimalBinding
import com.bumptech.glide.Glide
import com.app.zooapp.R

class ListAdapter(
    private val onItemClick: (AnimalEntity) -> Unit
) : RecyclerView.Adapter<ListAdapter.AnimalViewHolder>() {
    private var animals = listOf<AnimalEntity>()

    fun submitList(list: List<AnimalEntity>) {
        animals = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AnimalViewHolder {
        val binding = ItemAnimalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnimalViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
        holder.bind(animals[position])
    }

    override fun getItemCount(): Int = animals.size

    class AnimalViewHolder(
        private val binding: ItemAnimalBinding,
        private val onItemClick: (AnimalEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(animal: AnimalEntity) {
            binding.tvAnimalName.text = animal.nombre
            binding.tvAnimalSpecies.text = animal.especie
            Glide.with(binding.ivAnimalImage.context)
                .load(animal.imagen)
                .placeholder(R.drawable.ic_animal_placeholder)
                .into(binding.ivAnimalImage)
            itemView.setOnClickListener {
                onItemClick(animal)
            }
        }
    }
}