package com.helios.kmmfile

import platform.Foundation.NSURL
import platform.Foundation.URLByAppendingPathComponent

/**
 * Created by phpduy99 on 11/13/2022
 */
actual class PathComponent actual constructor(actual val component: String?) {

    val url: NSURL? =
        if (component != null) NSURL.fileURLWithPath(component).standardizedURL else null

    /**
     * Create a new [PathComponent] by appending [component] string.
     */
    actual fun byAppending(component: String): PathComponent? {
        url?.let {
            return PathComponent(it.URLByAppendingPathComponent(component)?.path)
        }
        return null
    }
}