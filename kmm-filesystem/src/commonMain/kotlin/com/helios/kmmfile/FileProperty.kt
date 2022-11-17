package com.helios.kmmfile

/**
 * Created by phpduy99 on 11/13/2022
 */

/**
 * Stats for a file or directory with name as [name].
 */
data class FileProperty(
    /**
     * The file's name including extension if any.
     */
    val name: String,

    /**
     * The file's extension if any
     */

    val extension: String?,

    /**
     * Absolute path to the file
     */

    val absolutePath: PathComponent,

    /**
     * Canonical path to the file. May differ from the input
     */
    val canonicalPath: PathComponent,

    /**
     * Date on which file was created.
     *
     * Note: On Android, this is always 0 as creation date is unavailable on all supported versions.
     */
    val createdAt: Long? = null,

    /**
     * Date when file was last modified.
     */
    val modifiedAt: Long? = null,

    /**
     * Size of the file in bytes
     */
    val size: Long? = null,

    /**
     * Type of the file.
     */
    val type: FileType
)