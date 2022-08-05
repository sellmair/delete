package integrationTest

import io.sellmair.delete.pathUtils.currentWorkingDirectory
import okio.FileSystem.Companion.SYSTEM
import okio.Path
import kotlin.test.Test
import kotlin.test.assertTrue

class DeleteIntegrationTest {
    @Test
    fun `delete directories test`() {
        val out = SYSTEM.currentWorkingDirectory.resolve("integrationTestOut")
        assertTrue(SYSTEM.exists(out), "Missing output directory at $out")
        val root1 = out.resolve("root1")
        val root2 = out.resolve("root2")

        createFiles(root1)
        createFiles(root2)
    }

    private fun createFiles(
        root: Path, levels: Int = 6, directoriesPerDirectory: Int = 6, filesPerDirectory: Int = 100
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

        if(levels == 1)
        for (fileNumber in 1..filesPerDirectory) {
            val file = root.resolve("$fileNumber.bin")
            SYSTEM.write(file, false) { writeUtf8("2801") }
        }
    }
}
