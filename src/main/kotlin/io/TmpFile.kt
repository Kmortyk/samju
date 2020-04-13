package io

import java.io.File
import java.io.FileOutputStream

class TmpFile {

    companion object {

        fun put(name: String, format: String, bts: ByteArray) : File {
            val tempFile = File.createTempFile("$name-", ".$format")
            tempFile.deleteOnExit()

            val stream = FileOutputStream(tempFile)
            stream.write(bts)
            return tempFile
        }
    }

}