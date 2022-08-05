package io.sellmair.delete.pathUtils

import okio.FileSystem
import okio.Path

fun FileSystem.isParent(parent: Path, child: Path): Boolean {
    if (parent == child) return false

    val absoluteParent = toAbsolutePath(parent).normalized()
    val absoluteChild = toAbsolutePath(child).normalized()
    if (absoluteParent == absoluteChild) return false

    val parentSegments = absoluteParent.segments
    val childSegments = absoluteChild.segments

    if (parentSegments.size >= childSegments.size) return false
    return childSegments.subList(0, parentSegments.size) == parentSegments
}
