package com.helios.kmmfile

import java.io.File

/**
 * Created by phpduy99 on 11/13/2022
 */
actual class PathComponent actual constructor(actual val component: String?) {
    private val file = if (component != null) File(component).canonicalFile else null

    /**
     * Create a new [PathComponent] by appending [component] string.
     */
    actual fun byAppending(component: String): PathComponent? {
        if (file == null) return null
        return PathComponent(File(file.absolutePath, component).canonicalFile.absolutePath)
    }
}