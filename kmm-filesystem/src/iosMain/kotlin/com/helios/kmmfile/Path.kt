package com.helios.kmmfile

import platform.Foundation.NSURL

/**
 * Created by phpduy99 on 11/13/2022
 */
actual class Path actual constructor() {

    constructor(absolutePath: String?, relativePath: String?) : this() {
        this.absolutePath = PathComponent(absolutePath)
        this.relativePath = PathComponent(relativePath)
    }

    /**
     * Absolute path to the resource
     */
    actual var absolutePath: PathComponent? = null
        private set

    /**
     * Relative Path to the resource
     */
    actual var relativePath: PathComponent? = null
        private set

    companion object {
        fun fromUrl(url: NSURL?) = Path(url?.path, url?.relativePath)

        fun fromUrlString(urlString: String): Path {
            val url = NSURL.fileURLWithPath(urlString).standardizedURL

            return fromUrl(url)
        }
    }
}