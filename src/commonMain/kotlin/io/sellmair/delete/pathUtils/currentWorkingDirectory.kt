package io.sellmair.delete.pathUtils

import okio.FileSystem
import okio.Path.Companion.toPath

val FileSystem.currentWorkingDirectory get() = canonicalize(".".toPath())
