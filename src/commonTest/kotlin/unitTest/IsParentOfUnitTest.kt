package unitTest

import fileSystemUtils.createFakeFileSystem
import io.sellmair.delete.pathUtils.currentWorkingDirectory
import io.sellmair.delete.pathUtils.isParent
import okio.FileSystem
import okio.Path.Companion.toPath
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class IsParentOfUnitTest {

    @Test
    fun `relative paths`() {
        assertTrue(FileSystem.SYSTEM.isParent("a/b".toPath(), "a/b/c".toPath()))
        assertTrue(FileSystem.SYSTEM.isParent("a/b".toPath(), "a/b/c/d".toPath()))
        assertFalse(FileSystem.SYSTEM.isParent("a/b".toPath(), "a/b".toPath()))
        assertFalse(FileSystem.SYSTEM.isParent("a/b/c".toPath(), "a/b".toPath()))
    }

    @Test
    fun `absolute paths`() {
        assertTrue(FileSystem.SYSTEM.isParent("/".toPath(), "/a".toPath()))
        assertTrue(FileSystem.SYSTEM.isParent("/".toPath(), "/a/b".toPath()))
        assertTrue(FileSystem.SYSTEM.isParent("/a".toPath(), "/a/b".toPath()))
        assertFalse(FileSystem.SYSTEM.isParent("/".toPath(), "/".toPath()))
        assertFalse(FileSystem.SYSTEM.isParent("/a".toPath(), "/".toPath()))
        assertFalse(FileSystem.SYSTEM.isParent("/a/b".toPath(), "/".toPath()))
        assertFalse(FileSystem.SYSTEM.isParent("/a/b".toPath(), "/a".toPath()))
        assertFalse(FileSystem.SYSTEM.isParent("/a/b/c".toPath(), "/a/b".toPath()))
    }

    @Test
    fun `absolute and relative paths`() {
        val fileSystem = createFakeFileSystem(workingDirectory = "/a/b/c".toPath())
        assertEquals(fileSystem.currentWorkingDirectory, fileSystem.workingDirectory)

        assertTrue(fileSystem.isParent("/a".toPath(), "d".toPath()))
        assertFalse(fileSystem.isParent("/x".toPath(), "d".toPath()))

        assertTrue(fileSystem.isParent("d".toPath(), "/a/b/c/d/e".toPath()))
        assertFalse(fileSystem.isParent("d".toPath(), "/x/b/c/d/e".toPath()))
    }
}
