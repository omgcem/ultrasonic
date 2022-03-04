package org.moire.ultrasonic.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.drakeet.multitype.ItemViewBinder
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.moire.ultrasonic.R
import org.moire.ultrasonic.domain.Identifiable
import org.moire.ultrasonic.domain.MusicDirectory
import org.moire.ultrasonic.service.DownloadFile
import org.moire.ultrasonic.service.Downloader
import timber.log.Timber

class BookmarkViewBinder(
    onItemClick: (DownloadFile) -> Unit,
    onContextMenuClick: ((MenuItem, DownloadFile) -> Boolean)? = null,
    checkable: Boolean,
    draggable: Boolean,
    context: Context,
    lifecycleOwner: LifecycleOwner,
) : TrackViewBinder(onItemClick, onContextMenuClick, checkable, draggable, context, lifecycleOwner) {
    override val contextMenuLayout = R.menu.context_menu_bookmark
}
