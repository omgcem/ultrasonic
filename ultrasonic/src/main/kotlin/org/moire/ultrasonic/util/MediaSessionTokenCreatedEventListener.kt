package org.moire.ultrasonic.util

import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat

/**
 * Callback interface for MediaSession related event subscribers
 */
interface MediaSessionEventListener {
    fun onMediaSessionTokenCreated(token: MediaSessionCompat.Token)
    fun onPlayFromMediaIdRequested(mediaId: String?, extras: Bundle?)
    fun onPlayFromSearchRequested(query: String?, extras: Bundle?)
}
