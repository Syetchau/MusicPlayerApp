package com.example.musicplayerapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Song(
    val songTitle: String?,
    val songArtist: String?,
    val songUri: String?,
    val songDuration: String?
): Parcelable