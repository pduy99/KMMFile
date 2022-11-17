package com.helios.kmmfile

/**
 * Created by phpduy99 on 11/13/2022
 */

expect class Path() {
    /**
     * Absolute path to the resource
     */
    var absolutePath: PathComponent?
        private set

    /**
     * Relative Path to the resource
     */
    var relativePath: PathComponent?
        private set
}