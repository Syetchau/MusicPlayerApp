package com.example.musicplayerapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerapp.databinding.AdapterLayoutSongItemBinding
import com.example.musicplayerapp.fragments.MusicListFragmentDirections
import com.example.musicplayerapp.model.Song

class SongAdapter: RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    inner class SongViewHolder(val itemBinding: AdapterLayoutSongItemBinding):
        RecyclerView.ViewHolder(itemBinding.root)

    private val differCallBack = object: DiffUtil.ItemCallback<Song>(){
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.songUri == newItem.songUri
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder(
            AdapterLayoutSongItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val currentSong = differ.currentList[position]

        holder.itemBinding.apply{
            tvDuration.text = currentSong.songDuration
            songTitle.text = currentSong.songTitle
            songArtist.text = currentSong.songArtist
            tvOrder.text = "${position + 1}"
        }

        holder.itemView.setOnClickListener { mView ->
            val direction = MusicListFragmentDirections
                .actionMusicListFragmentToMusicPlayFragment(currentSong)
            mView.findNavController().navigate(direction)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}