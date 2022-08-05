package unitTest

import fileSystemUtils.createFakeFileSystem
import io.sellmair.delete.Event
import io.sellmair.delete.delete
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import kotlin.test.*

class DeleteUnitTest {
    private val fileSystem = createFakeFileSystem()
    private val eventChannel = Channel<Event>(Channel.UNLIMITED)

    @AfterTest
    fun close() {
        fileSystem.checkNoOpenFiles()
    }

    @Test
    fun `delete single regular file`() = runTest {
        val regularFilePath = "regularFilePath".toPath()
        fileSystem.write(regularFilePath, true) { writeUtf8("D0") }

        assertTrue(fileSystem.exists(regularFilePath))
        assertTrue(fileSystem.metadata(regularFilePath).isRegularFile)

        delete(fileSystem, setOf(regularFilePath), eventChannel)
        eventChannel.close()

        assertFalse(fileSystem.exists(regularFilePath))

        assertEquals(
            listOf(Event.FilesDiscovered(1), Event.FilesDeleted(1)), eventChannel.toList()
        )
    }

    @Test
    fun `delete two regular files`() = runTest {
        val regularFilePath1 = "regularFilePath1".toPath()
        val regularFilePath2 = "regularFilePath2".toPath()

        fileSystem.write(regularFilePath1, true) { writeUtf8("D0") }
        fileSystem.write(regularFilePath2, true) { writeUtf8("D0") }

        delete(fileSystem, setOf(regularFilePath1, regularFilePath2), eventChannel)
        eventChannel.close()

        assertFalse(fileSystem.exists(regularFilePath1))
        assertFalse(fileSystem.exists(regularFilePath2))

        assertEquals(
            listOf(
                Event.FilesDiscovered(2), Event.FilesDeleted(2),
            ), eventChannel.toList()
        )
    }

    @Test
    fun `delete non-existent file`() = runTest {
        delete(fileSystem, setOf("any".toPath()), eventChannel)
        eventChannel.close()

        assertEquals(
            listOf(Event.FilesDiscovered(0), Event.FilesDeleted(0)), eventChannel.toList()
        )
    }

    @Test
    fun `delete two level directory`() = runTest {
        val directory = "directory".toPath()
        val childDirectory = directory.resolve("childDirectory")
        val directoryFile = directory.resolve("file")
        val childDirectoryFile = childDirectory.resolve("file")

        fileSystem.createDirectories(directory, true)
        fileSystem.createDirectories(childDirectory, true)
        fileSystem.sink(directoryFile, true).close()
        fileSystem.sink(childDirectoryFile, true).close()

        assertTrue(fileSystem.metadata(directory).isDirectory)
        assertTrue(fileSystem.metadata(childDirectory).isDirectory)
        assertTrue(fileSystem.metadata(directoryFile).isRegularFile)
        assertTrue(fileSystem.metadata(childDirectoryFile).isRegularFile)

        delete(fileSystem, setOf(directory), eventChannel)
        eventChannel.close()

        assertFalse(fileSystem.exists(directory))
        assertFalse(fileSystem.exists(childDirectory))
        assertFalse(fileSystem.exists(directoryFile))
        assertFalse(fileSystem.exists(childDirectoryFile))
    }

    @Test
    fun `delete two level directory - provide child and root directories as argument`() = runTest {
        val directory = "directory".toPath()
        val childDirectory = directory.resolve("childDirectory")
        val directoryFile = directory.resolve("file")
        val childDirectoryFile = childDirectory.resolve("file")

        fileSystem.createDirectories(directory, true)
        fileSystem.createDirectories(childDirectory, true)
        fileSystem.sink(directoryFile, true).close()
        fileSystem.sink(childDirectoryFile, true).close()

        delete(fileSystem, setOf(directory, childDirectory), eventChannel)
        eventChannel.close()
        assertFalse(fileSystem.exists(directory))

        val events = eventChannel.toList()

        val discovered = events.filterIsInstance<Event.FilesDiscovered>()
            .map { it.count }.reduce(Int::plus)

        val deleted = events.filterIsInstance<Event.FilesDeleted>()
            .map { it.count }.reduce(Int::plus)

        assertEquals(4, discovered)
        assertEquals(4, deleted)
    }
}

