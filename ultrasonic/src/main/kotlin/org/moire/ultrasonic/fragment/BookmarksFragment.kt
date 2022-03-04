/*
 * BookmarksFragment.kt
 * Copyright (C) 2009-2021 Ultrasonic developers
 *
 * Distributed under terms of the GNU GPLv3 license.
 */

package org.moire.ultrasonic.fragment

import android.os.Bundle
import android.view.View
import android.view.MenuItem
import android.widget.CheckedTextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.core.view.isVisible
import kotlinx.coroutines.launch
import org.moire.ultrasonic.R
import org.moire.ultrasonic.adapters.BaseAdapter
import org.moire.ultrasonic.domain.MusicDirectory
import org.moire.ultrasonic.service.MusicServiceFactory.getMusicService
import org.moire.ultrasonic.adapters.BookmarkViewBinder
import org.moire.ultrasonic.fragment.FragmentTitle.Companion.setTitle
import timber.log.Timber

/**
 * Lists the Bookmarks available on the server
 *
 * Bookmarks allows to save the play position of tracks, especially useful for longer tracks like
 * audio books etc.
 *
 * Therefore this fragment allows only for singular selection and playback.
 */
class BookmarksFragment : TrackCollectionFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitle(this, R.string.button_bar_bookmarks)
        viewAdapter.selectionType = BaseAdapter.SelectionType.SINGLE
        viewAdapter.register(
            BookmarkViewBinder(
                onItemClick = { onItemClick(it.song) },
                onContextMenuClick = { menu, id -> onContextMenuItemSelected(menu, id.song) },
                checkable = false,
                draggable = false,
                context = requireContext(),
                lifecycleOwner = viewLifecycleOwner
            )
        )
    }

    override fun getLiveData(
        args: Bundle?,
        refresh: Boolean
    ): LiveData<List<MusicDirectory.Child>> {
        listModel.viewModelScope.launch(handler) {
            refreshListView?.isRefreshing = true
            listModel.getBookmarks()
            refreshListView?.isRefreshing = false
        }
        return listModel.currentList
    }

    /**
     * Set a custom listener to perform the playing, in order to be able to restore
     * the playback position
     */
    override fun setupButtons(view: View) {
        super.setupButtons(view)

        playNowButton!!.setOnClickListener {
            playNow(getSelectedSongs())
        }
    }

    override fun onItemClick(item: MusicDirectory.Child) {
        playNow(getClickedSong(item))
    }

    internal fun getClickedSong(item: MusicDirectory.Child): List<MusicDirectory.Entry> {
        //This can probably be done better
        return viewAdapter.getCurrentList().mapNotNull {
            if (it is MusicDirectory.Entry && (it.id == item.id))
                it
            else
                null
        }
    }

    override fun onContextMenuItemSelected(
        menuItem: MenuItem,
        item: MusicDirectory.Child
    ): Boolean {
        val entryId = item.id
        when (menuItem.itemId) {
            R.id.song_menu_play_now -> {
                onItemClick(item)
            }
            R.id.bookmark_menu_delete -> {
                Thread {
                    val musicService = getMusicService()
                    try {
                        musicService.deleteBookmark(item.id)
                        getLiveData(arguments, true)
                    } catch (all: Exception) {
                        Timber.e(all, all.stackTraceToString())
                    }
                }.start()
            }
            R.id.bookmark_menu_multiselect -> {
                //switch to multiselect mode
            }
            else -> {
                return super.onContextMenuItemSelected(menuItem, item)
            }
        }
        return true
    }

    /**
     * Custom playback function which uses the restore functionality. A bit of a hack..
     */
    private fun playNow(songs: List<MusicDirectory.Entry>) {
        if (songs.isNotEmpty()) {

            val position = songs[0].bookmarkPosition

            mediaPlayerController.restore(
                songs = songs,
                currentPlayingIndex = 0,
                currentPlayingPosition = position,
                autoPlay = true,
                newPlaylist = true
            )
        }
    }
}
