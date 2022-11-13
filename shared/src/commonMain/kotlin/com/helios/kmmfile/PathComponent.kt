package com.helios.kmmfile

/**
 * Created by phpduy99 on 11/13/2022
 */
expect class PathComponent(component: String?) {
    /**
     * Path used to create the component.
     */
    val component: String?

    /**
     * Create a new [PathComponent] by appending [component] string.
     */
    fun byAppending(component: String): PathComponent?
}