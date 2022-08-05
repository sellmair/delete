package fileSystemUtils

import okio.Path
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem

fun createFakeFileSystem(workingDirectory: Path? = null): FakeFileSystem {
    return FakeFileSystem().apply {
        if (workingDirectory != null) this.workingDirectory = workingDirectory

        // Ensure working directory exists
        createDirectories(workingDirectory ?: ".".toPath(), false)
    }
}
