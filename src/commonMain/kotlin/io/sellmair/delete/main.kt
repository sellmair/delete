package io.sellmair.delete

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path.Companion.toPath

fun main(args: Array<String>) {
    runBlocking {
        val paths = args.map { it.toPath(true) }.toSet()
        val workingDispatcher = newFixedThreadPoolContext(16, "Working Dispatcher")
        val eventChannel = Channel<Event>(1024)

        launch {
            terminal(Console.System, eventChannel)
        }

        withContext(workingDispatcher) {
            delete(FileSystem.SYSTEM, paths, eventChannel)
        }

        eventChannel.send(Event.Done)
    }
}
