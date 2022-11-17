package com.helios.kmmfile

import android.net.Uri
import java.io.File

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
        fun fromUrl(url: Uri?) = Path(url?.path, url?.path)

        fun fromUrlString(urlString: String): Path {
            val uri = Uri.fromFile(File(urlString))

            return fromUrl(uri)
        }
    }
}