package integrationTest

import io.sellmair.delete.Event
import io.sellmair.delete.delete
import io.sellmair.delete.pathUtils.currentWorkingDirectory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.test.runTest
import okio.FileSystem.Companion.SYSTEM
import okio.Path
import kotlin.coroutines.CoroutineContext
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.fail

class DeleteIntegrationTest {

    @Test
    fun `delete directories test`() {
        val out = SYSTEM.currentWorkingDirectory.resolve("build/integrationTestOut")
        SYSTEM.createDirectories(out, false)
        assertTrue(SYSTEM.exists(out), "Missing output directory at $out")
        val root1 = out.resolve("root1")
        val root2 = out.resolve("root2")

        createFiles(root1)
        createFiles(root2)

        assertTrue(SYSTEM.exists(root1))
        assertTrue(SYSTEM.exists(root2))

        runTest {
            val allEvents = produce {
                delete(SYSTEM, setOf(root1, root2), channel)
            }.toList().toSet()
            if (allEvents.isEmpty()) fail("No events received")
            assertTrue(allEvents.contains(Event.FilesDiscovered(13)), "missing files discovered event: $allEvents")
            assertTrue(allEvents.contains(Event.FilesDeleted(13)), "Missing deleted event")
        }
    }

    private fun createFiles(
        root: Path, levels: Int = 3, directoriesPerDirectory: Int = 3, filesPerDirectory: Int = 10
    ) {
        SYSTEM.createDirectories(root, false)
        for (directoryNumber in 1..directoriesPerDirectory) {
            val directory = root.resolve("$directoryNumber")
            SYSTEM.createDirectories(directory, false)
            if (levels > 1) {
                createFiles(
                    directory, levels = levels - 1,
                    directoriesPerDirectory = directoriesPerDirectory,
                    filesPerDirectory = filesPerDirectory
                )
            }
        }

        if (levels == 1)
            for (fileNumber in 1..filesPerDirectory) {
                val file = root.resolve("$fileNumber.bin")
                SYSTEM.write(file, false) { writeUtf8("2801") }
            }
    }
}
