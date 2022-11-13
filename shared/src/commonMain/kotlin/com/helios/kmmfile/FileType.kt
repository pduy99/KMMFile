package com.helios.kmmfile

/**
 * Created by phpduy99 on 11/13/2022
 */
/**
 * Type of a file
 */
enum class FileType {
    /**
     * A regular file like text or pdf file.
     */
    File,

    /**
     * A directory.
     */
    Directory,

    /**
     * Platforms may support some special files. All those are marked as [Unknown].
     */
    Unknown
}