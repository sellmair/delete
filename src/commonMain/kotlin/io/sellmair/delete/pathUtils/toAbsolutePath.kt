package io.sellmair.delete.pathUtils

import okio.FileSystem
import okio.Path

fun FileSystem.toAbsolutePath(path: Path): Path {
    if (path.isAbsolute) return path
    return currentWorkingDirectory.resolve(path)
}
