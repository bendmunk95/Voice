package de.ph1b.audiobook.playback.utils

import com.google.android.exoplayer2.*
import de.ph1b.audiobook.playback.PlayerState

fun SimpleExoPlayer.setPlaybackSpeed(speed: Float) {
  playbackParameters = PlaybackParameters(speed, 1F)
}

inline fun ExoPlayer.onStateChanged(crossinline action: (PlayerState) -> Unit) {
  addListener(
    object : Player.DefaultEventListener() {
      override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        val state = when (playbackState) {
          Player.STATE_ENDED -> PlayerState.ENDED
          Player.STATE_IDLE -> PlayerState.IDLE
          Player.STATE_READY, Player.STATE_BUFFERING -> {
            if (playWhenReady) PlayerState.PLAYING
            else PlayerState.PAUSED
          }
          else -> null
        }
        if (state != null) action(state)
      }
    }
  )
}

inline fun ExoPlayer.onError(crossinline action: (ExoPlaybackException) -> Unit) {
  addListener(
    object : Player.DefaultEventListener() {
      override fun onPlayerError(error: ExoPlaybackException) {
        action(error)
      }
    }
  )
}

inline fun SimpleExoPlayer.onAudioSessionId(crossinline action: (Int) -> Unit) {
  setAudioDebugListener(
    object : SimpleAudioRendererEventListener {
      override fun onAudioSessionId(audioSessionId: Int) {
        action(audioSessionId)
      }
    }
  )
}

inline fun ExoPlayer.onPositionDiscontinuity(crossinline action: () -> Unit) {
  addListener(
    object : Player.DefaultEventListener() {
      override fun onPositionDiscontinuity(reason: Int) {
        action()
      }
    }
  )
}
