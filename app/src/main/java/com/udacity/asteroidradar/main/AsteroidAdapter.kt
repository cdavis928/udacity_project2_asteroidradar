package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.database.Asteroid
import com.udacity.asteroidradar.databinding.AsteroidListItemBinding

class AsteroidAdapter(val clickListener: AsteroidListener) :
    ListAdapter<Asteroid, AsteroidAdapter.ViewHolder>(AsteroidDiffCallBack()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val asteroidItem = getItem(position)
        holder.bind(clickListener, asteroidItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: AsteroidListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: AsteroidListener, item: Asteroid) {
            binding.asteroid = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = AsteroidListItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }

    // A click listener to register the click for moving to the detail fragment
    class AsteroidListener(val clickListener: (asteroid: Asteroid) -> Unit) {
        fun onClick(asteroid: Asteroid) = clickListener(asteroid)
    }
}

class AsteroidDiffCallBack : DiffUtil.ItemCallback<Asteroid>() {
    override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
        return oldItem == newItem
    }
}