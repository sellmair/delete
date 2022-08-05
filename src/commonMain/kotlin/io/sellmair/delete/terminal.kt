package io.sellmair.delete

import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okio.Path

interface Console {
    fun println(message: String)

    object System : Console {
        override fun println(message: String) = kotlin.io.println(message)
    }
}

suspend fun terminal(
    console: Console,
    events: ReceiveChannel<Event>
) {
    var currentWorkingDir: Path? = null
    var deletedFiles = 0
    var pendingFiles = 0
    coroutineScope {
        launch {
            while (true) {
                delay(500)
                console.println("Deleted: $deletedFiles | Pending: $pendingFiles")
                currentWorkingDir?.let {
                    console.println(it.toString())
                }
            }
        }

        events.consumeEach { event ->
            when (event) {
                is Event.FilesDeleted -> {
                    pendingFiles -= event.count
                    deletedFiles += event.count
                }

                is Event.FilesDiscovered -> pendingFiles += event.count
                is Event.CurrentWorkingDirectory -> currentWorkingDir = event.path
                is Event.Done -> {
                    console.println("Deleted $deletedFiles files")
                    cancel()
                }
            }
        }

        cancel()
    }
}
