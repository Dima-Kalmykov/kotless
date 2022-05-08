package io.kotless.plugin.gradle.utils

import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.channels.Channels

internal object Downloads {

    fun download(binFile: File, version: String?, resourceType: ResourceType) {
        val parentFile = binFile.parentFile

        when (resourceType) {
            ResourceType.TERRAFORM -> {
                download(
                    URL("https://releases.hashicorp.com/terraform/$version/terraform_${version}_${CommandLine.os}.zip"),
                    parentFile,
                )
            }
            ResourceType.TF_LINT -> {
                download(
                    URL("https://github.com/wata727/tflint/releases/download/v$version/tflint_${CommandLine.os}.zip"),
                    parentFile,
                )
            }
        }
    }

    private fun download(url: URL, toFile: File) {
        toFile.parentFile.mkdirs()

        val archive = File(toFile.absolutePath + "." + Archive.ZIP)

        if (archive.exists()) {
            archive.delete()
        }

        transferFile(toFile, url)

        if (toFile.exists()) {
            toFile.deleteRecursively()
        }

        Archive.ZIP.unarchive(archive, toFile)

        archive.delete()
    }

    private fun transferFile(toFile: File, url: URL) {
        toFile.parentFile.mkdirs()

        FileOutputStream(toFile).channel.use { fileChannel ->
            val channel = Channels.newChannel(url.openStream())
            channel.use { byteChannel ->
                fileChannel.transferFrom(byteChannel, 0, Long.MAX_VALUE)
            }
        }
    }
}
