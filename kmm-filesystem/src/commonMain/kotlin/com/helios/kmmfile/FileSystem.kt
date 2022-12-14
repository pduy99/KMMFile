package com.helios.kmmfile

/**
 * Created by phpduy99 on 11/13/2022
 */
expect object FileSystem {

    /**
     * Path to the common contents directory. Directory is guaranteed to exist (created if not exists already).
     *
     * On Android, it points to `files` directory.
     * On iOS, it points to `Documents` directory.
     */
    val contentsDirectory: Path

    /**
     * Path to caches directory. Directory is guaranteed to exist (created if not exists already).
     */
    val cachesDirectory: Path

    /**
     * Path to Temporary directory. Directory is guaranteed to exist (created if not exists already).
     */
    val temporaryDirectory: Path

    /**
     * Returns a list of stats for the contents of directory at `path`.
     */
    fun readDir(path: String): List<FileProperty>?

    /**
     * Returns a list of stats for the contents of directory at `pathComponent`.
     */
    fun readDir(pathComponent: PathComponent): List<FileProperty>?

    /**
     * Returns stats for the resource at [path].
     */
    fun property(path: String): FileProperty?

    /**
     * Returns stats for the resource at `pathComponent`.
     */
    fun property(pathComponent: PathComponent): FileProperty?

    /**
     *
     * Returns the contents of the file located at `path`. The content is parsed according to `encoding`.
     * For binary files, use `encoding` = [ContentEncoding.Base64].
     *
     */
    fun readFile(path: String, encoding: ContentEncoding = ContentEncoding.UTF_8): String?

    /**
     *
     * Returns the contents of the file located at `pathComponent`. The content is parsed according to `encoding`.
     * For binary files, use `encoding` = [ContentEncoding.Base64].
     *
     */
    fun readFile(
        pathComponent: PathComponent,
        encoding: ContentEncoding = ContentEncoding.UTF_8
    ): String?

    /**
     * Returns the contents of the file located at `path` as ByteArray.
     */
    fun readFileAsByteArray(path: String): ByteArray?

    /**
     * Returns the contents of the file located at `pathComponent` as ByteArray.
     */
    fun readFileAsByteArray(pathComponent: PathComponent): ByteArray?

    /**
     * Writes `contents` to the file located at `path`. If `create` is true, then file is created if it does not exist.
     * For binary files, use `encoding` = [ContentEncoding.Base64].
     * * Returns true if operation is successful, otherwise false.
     */
    fun writeFile(
        path: String,
        contents: String,
        create: Boolean = false,
        encoding: ContentEncoding = ContentEncoding.UTF_8,
        append: Boolean
    ): Boolean

    /**
     * Writes `contents` to the file located at `pathComponent`.
     * If `create` is true, then file is created if it does not exist.
     * For binary files, use `encoding` = [ContentEncoding.Base64].
     * Returns true if operation is successful, otherwise false.
     */
    fun writeFile(
        pathComponent: PathComponent,
        contents: String,
        create: Boolean = false,
        encoding: ContentEncoding = ContentEncoding.UTF_8,
        append: Boolean
    ): Boolean

    /**
     * Writes `contents` to the file located at `path`. If `create` is true, then file is created if it does not exist.
     * Returns true if operation is successful, otherwise false.
     */
    fun writeFile(path: String, contents: ByteArray, create: Boolean, append: Boolean): Boolean

    /**
     * Writes `contents` to the file located at `pathComponent`. If `create` is true, then file is created if it does not exist.
     * Returns true if operation is successful, otherwise false.
     */
    fun writeFile(
        pathComponent: PathComponent,
        contents: ByteArray,
        create: Boolean,
        append: Boolean
    ): Boolean

    /**
     * Creates a file at `path` if it does not exist.
     * Returns false if file already exists, otherwise true.
     */
    fun touch(path: String): Boolean

    /**
     * Creates a file at `pathComponent` if it does not exist.
     * Returns false if file already exists, otherwise true.
     */
    fun touch(pathComponent: PathComponent): Boolean

    /**
     * Creates a directory on `path`.
     * If `recursive` is true, then intermediate directories are also created.
     * Returns true if directory is created successfully.
     */
    fun mkdir(path: String, recursive: Boolean): Boolean

    /**
     * Creates a directory on `pathComponent`.
     * If `recursive` is true, then intermediate directories are also created.
     * Returns true if directory is created successfully.
     */
    fun mkdir(pathComponent: PathComponent, recursive: Boolean): Boolean

    /**
     * Returns true if the file or directory exists at `path`.
     */
    fun exists(path: String): Boolean

    /**
     * Returns true if the file or directory exists at `pathComponent`.
     */
    fun exists(pathComponent: PathComponent): Boolean

    /**
     * Removes a file on `path`.
     * If it is a directory, its contents are removed as well.
     * Returns true if file is deleted successfully, otherwise false.
     */
    fun remove(path: String): Boolean

    /**
     * Removes a file on `pathComponent`.
     * If it is a directory, its contents are removed as well.
     * Returns true if file is deleted successfully, otherwise false.
     */
    fun remove(pathComponent: PathComponent): Boolean

    /**
     * Moves the file from `srcPath` to `destPath`.
     * If `srcPath` is a directory, its contents including hidden files are moved.
     * Returns true if the move is successful, otherwise false.
     */
    fun moveFile(srcPath: String, destPath: String): Boolean

    /**
     * Moves the file from `srcPathComponent` to `destPathComponent`.
     * If `srcPathComponent` is a directory, its contents including hidden files are moved.
     * Returns true if the move is successful, otherwise false.
     */
    fun moveFile(srcPathComponent: PathComponent, destPathComponent: PathComponent): Boolean


    /**
     * Copies the file from `srcPath` to `destPath`.
     * If `srcPath` is a directory, its contents including hidden files are copied.
     * Returns true if the copy is successful, otherwise false.
     */
    fun copyFile(srcPath: String, destPath: String): Boolean

    /**
     * Copies the file from `srcPathComponent` to `destPathComponent`.
     * If `srcPathComponent` is a directory, its contents including hidden files are copied.
     * Returns true if the copy is successful, otherwise false.
     */
    fun copyFile(srcPathComponent: PathComponent, destPathComponent: PathComponent): Boolean

}