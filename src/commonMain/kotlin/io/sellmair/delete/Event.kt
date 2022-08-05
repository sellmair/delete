package io.sellmair.delete

import okio.Path

sealed interface Event {
    data class CurrentWorkingDirectory(val path: Path) : Event
    data class FilesDiscovered(val count: Int) : Event
    data class FilesDeleted(val count: Int) : Event
    object Done : Event
}
