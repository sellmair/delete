package io.sellmair.delete

import io.sellmair.delete.pathUtils.isParent
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okio.FileSystem
import okio.Path

suspend fun delete(
    fileSystem: FileSystem, paths: Set<Path>, eventChannel: SendChannel<Event>
) {
    suspend fun runDeleteRecursively(path: Path) {
        val metadata = fileSystem.metadataOrNull(path) ?: return

        if (metadata.isDirectory) {
            eventChannel.trySend(Event.CurrentWorkingDirectory(path))
            val children = fileSystem.list(path)
            eventChannel.send(Event.FilesDiscovered(children.size))
            coroutineScope {
                children.forEach { child ->
                    launch { runDeleteRecursively(child) }
                }
            }
            eventChannel.send(Event.FilesDeleted(children.size))
        }

        fileSystem.delete(path, false)
    }

    coroutineScope {
        val existingRootPaths = paths.map { it.normalized() }
            .filter { fileSystem.exists(it) }
            .filter { path -> paths.none { otherPath -> fileSystem.isParent(otherPath, path) } }

        eventChannel.send(Event.FilesDiscovered(existingRootPaths.size))

        existingRootPaths.forEach { path ->
            launch {
                runDeleteRecursively(path)
            }
        }

        eventChannel.send(Event.FilesDeleted(existingRootPaths.size))
    }
}
