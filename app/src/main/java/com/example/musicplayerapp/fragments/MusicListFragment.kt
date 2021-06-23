package com.example.musicplayerapp.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayerapp.R
import com.example.musicplayerapp.adapter.SongAdapter
import com.example.musicplayerapp.databinding.FragmentMusicListBinding
import com.example.musicplayerapp.helper.Constants
import com.example.musicplayerapp.helper.Constants.toast
import com.example.musicplayerapp.model.Song
import java.util.ArrayList

class MusicListFragment : Fragment(R.layout.fragment_music_list) {

    private var _binding: FragmentMusicListBinding?= null
    private val binding get() = _binding!!
    private var songList: MutableList<Song> = ArrayList()
    private lateinit var songAdapter: SongAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadSongs()
        setupRecyclerView()
        checkUserPermission()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            Constants.REQUEST_CODE_FOR_PERMISSION ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    activity?.toast("Permission Granted")
                    loadSongs()
                } else {
                    activity?.toast("Permission Denied")
                } else -> {
                      super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun checkUserPermission() {
        if (activity?.let {
            ActivityCompat.checkSelfPermission(
                it, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            } != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                Constants.REQUEST_CODE_FOR_PERMISSION
            )
            return
        }
        loadSongs()
    }

    private fun loadSongs() {
        val allSongUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        val sortOrder = " ${MediaStore.Audio.Media.DISPLAY_NAME} ASC"
        val cursor = activity?.applicationContext?.contentResolver!!.query(
            allSongUri, null, selection, null, sortOrder
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val songUri = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val songAuthor = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                val songTitle = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                val songDuration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))

                val songDurationLong = songDuration.toLong()
                songList.add(
                    Song(
                        songTitle, songAuthor, songUri, Constants.durationConverter(songDurationLong)
                    )
                )
            }
            cursor.close()
        } else{
            activity?.toast("No songs")
        }
    }

    private fun setupRecyclerView() {
        songAdapter = SongAdapter()

        binding.rvSongList.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            adapter = songAdapter
        }
        songAdapter.differ.submitList(songList)
        songList.clear()
    }
}