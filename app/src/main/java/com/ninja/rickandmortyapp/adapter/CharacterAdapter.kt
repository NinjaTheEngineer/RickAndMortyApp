package com.ninja.rickandmortyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ninja.rickandmortyapp.utils.ImageLoader
import com.ninja.rickandmortyapp.databinding.ItemCharacterBinding
import com.ninja.rickandmortyapp.model.Character

class CharacterAdapter(private val onItemClick: (Character) -> Unit) : RecyclerView.Adapter<CharacterAdapter.CharacterViewHolder>() {
    private val characterList = mutableListOf<Character>()
    private val imageLoader = ImageLoader()

    fun submitList(characters: List<Character>) {
        characterList.clear()
        characterList.addAll(characters)
        notifyDataSetChanged()
    }
    enum class SortType {
        GENDER,
        EPISODES,
        NAME
    }

    private var currentSortType: SortType? = null
    private var isAscending = true

    fun orderBy(sortType: SortType) {
        if (currentSortType == sortType) {
            isAscending = !isAscending
        } else {
            currentSortType = sortType
            isAscending = true
        }

        when (sortType) {
            SortType.GENDER -> characterList.sortWith(compareBy<Character> { it.gender == "Female" }.thenBy { it.name })
            SortType.EPISODES -> characterList.sortBy { it.episode.size }
            SortType.NAME -> characterList.sortBy { it.name }
        }

        if (!isAscending) {
            characterList.reverse()
        }

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCharacterBinding.inflate(inflater, parent, false)
        return CharacterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val character = characterList[position]
        holder.bind(character)
    }

    override fun getItemCount(): Int = characterList.size

    inner class CharacterViewHolder(private val binding: ItemCharacterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val character = characterList[position]
                    onItemClick(character)
                }
            }
        }

        fun bind(character: Character) {
            //binding.characterName.text = character.name
            imageLoader.loadIntoImageView(binding.root.context, character.image, binding.ivImage)
        }
    }
}