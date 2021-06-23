package com.example.musicplayerapp.fragments

import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import com.example.musicplayerapp.R
import com.example.musicplayerapp.databinding.FragmentMusicPlayBinding
import com.example.musicplayerapp.helper.Constants
import com.example.musicplayerapp.model.Song

class MusicPlayFragment : Fragment(R.layout.fragment_music_play) {

    private var _binding: FragmentMusicPlayBinding?= null
    private val binding get() = _binding!!
    private lateinit var song: Song
    private val args: MusicPlayFragmentArgs by navArgs()
    private var mediaPlayer: MediaPlayer?= null
    private var seekLength: Int = 0
    private val seekForwardTime = 5000
    private val seekBackwardTime = 5000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicPlayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        song = args.song!!
        mediaPlayer = MediaPlayer()

        initData()
    }

    override fun onDestroy() {
        super.onDestroy()
        clearMediaPlayer()
    }

    private fun initData() {
        binding.tvAuthor.text = song.songArtist
        binding.tvTitle.text = song.songTitle
        binding.tvDuration.text = song.songDuration

        binding.ibPlay.setOnClickListener {
            playSong()
        }

        binding.ibForwardSong.setOnClickListener{
            forwardSong()
        }

        binding.ibBackwardSong.setOnClickListener {
            backwardSong()
        }

        binding.ibRepeat.setOnClickListener {
            repeatSong()
        }

        displaySongArt()
    }

    private fun playSong() {
        if(!mediaPlayer!!.isPlaying) {
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(song.songUri)
            mediaPlayer!!.prepare()
            mediaPlayer!!.seekTo(seekLength)
            mediaPlayer!!.start()

            binding.ibPlay.setImageDrawable(ContextCompat.getDrawable(
                activity?.applicationContext!!, R.drawable.ic_pause))
            updateSeekBar()
        } else {
            mediaPlayer!!.pause()
            seekLength = mediaPlayer!!.currentPosition

            binding.ibPlay.setImageDrawable(ContextCompat.getDrawable(
                activity?.applicationContext!!, R.drawable.ic_play))
        }
    }

    private fun updateSeekBar() {
        if(mediaPlayer != null) {
            binding.tvCurrentTime.text = Constants.durationConverter(
                mediaPlayer!!.currentPosition.toLong()
            )
        }
        seekBarSetup()
        Handler().postDelayed(runnable, 50)
    }

    private var runnable = Runnable {
        updateSeekBar()
    }

    private fun seekBarSetup() {
        if(mediaPlayer != null) {
            binding.seekBar.progress = mediaPlayer!!.currentPosition
            binding.seekBar.max = mediaPlayer!!.duration
        }
        binding.seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer!!.seekTo(progress)
                    binding.tvCurrentTime.text = Constants.durationConverter(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                    if (seekBar != null) {
                        mediaPlayer!!.seekTo(seekBar.progress)
                    }
                }
            }
        })
    }

    private fun clearMediaPlayer() {
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.stop()
        }
        mediaPlayer!!.release()
        mediaPlayer = null
    }

    private fun forwardSong() {
        if (mediaPlayer != null) {
            val currentPosition: Int = mediaPlayer!!.currentPosition

            if (currentPosition + seekForwardTime <= mediaPlayer!!.duration) {
                mediaPlayer!!.seekTo(currentPosition + seekForwardTime)
            } else {
                mediaPlayer!!.seekTo(mediaPlayer!!.duration)
            }
        }
    }

    private fun backwardSong() {
        if (mediaPlayer != null) {
            val currentPosition: Int = mediaPlayer!!.currentPosition

            if (currentPosition - seekBackwardTime >= 0) {
                mediaPlayer!!.seekTo(currentPosition - seekBackwardTime)
            } else {
                mediaPlayer!!.seekTo(0)
            }
        }
    }

    private fun repeatSong() {
        if (!mediaPlayer!!.isLooping) {
            mediaPlayer!!.isLooping = true
            binding.ibRepeat.setImageDrawable(ContextCompat.getDrawable(
                activity?.applicationContext!!, R.drawable.ic_repeat_white
            ))
        } else {
            mediaPlayer!!.isLooping = false
            binding.ibRepeat.setImageDrawable(ContextCompat.getDrawable(
                activity?.applicationContext!!, R.drawable.ic_repeat
            ))
        }
    }

    private fun displaySongArt() {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(song.songUri)
        val data = mediaMetadataRetriever.embeddedPicture
        if(data != null) {
            val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
            binding.ibCover.setImageBitmap(bitmap)
        }
    }
}