package io

import java.io.ByteArrayInputStream
import java.util.concurrent.CountDownLatch
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.LineEvent


class SimplePlayer {

    companion object {
        fun play(decoded: ByteArray) {

            val audioIn = AudioSystem.getAudioInputStream(
                ByteArrayInputStream(decoded)
            )
            val song = AudioSystem.getClip()
            song.open(audioIn)
            song.start()

            val latch = CountDownLatch(1)
            song.addLineListener { event ->
                if (event.type == LineEvent.Type.STOP) {
                    event.line.close()
                    latch.countDown()
                }
            }
            latch.await()
        }
    }
}