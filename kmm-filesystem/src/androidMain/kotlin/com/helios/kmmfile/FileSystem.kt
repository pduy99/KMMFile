package com.helios.kmmfile

import android.content.Context
import android.util.Base64
import java.io.*
import java.lang.ref.WeakReference

/**
 * Created by phpduy99 on 11/13/2022
 */
actual object FileSystem {

    private var context = WeakReference<Context>(null)

    fun init(context: Context) {
        this.context = WeakReference(context)
    }

    /**
     * Path to the common contents directory. Directory is guaranteed to exist (created if not exists already).
     *
     * On Android, it points to `files` directory.
     * On iOS, it points to `Documents` directory.
     */
    actual val contentsDirectory: Path
        get() {
            return executeIfNotNull(context.get()) {
                val path = it.filesDir.absolutePath
                File(path).mkdir()

                return Path(path, path)
            }
        }

    /**
     * Path to caches directory. Directory is guaranteed to exist (created if not exists already).
     */
    actual val cachesDirectory: Path
        get() {
            return executeIfNotNull(context.get()) {
                val path = it.filesDir.absolutePath
                File(path).mkdir()

                return Path(path, path)
            }
        }

    /**
     * Path to Temporary directory. Directory is guaranteed to exist (created if not exists already).
     */
    actual val temporaryDirectory: Path
        get() = cachesDirectory

    /**
     * Returns a list of file property for the contents of directory at `path`.
     */
    actual fun readDir(path: String): List<FileProperty>? {
        val parent = File(path).canonicalFile ?: return null
        val fileList = File(path).listFiles() ?: return null

        return fileList.map {
            buildFileProperty(it)
        }
    }

    /**
     * Returns a list of file property for the contents of directory at `pathComponent`.
     */
    actual fun readDir(pathComponent: PathComponent): List<FileProperty>? {
        val path = pathComponent.component ?: return null

        return readDir(path)
    }

    /**
     * Returns stats for the resource at [path].
     */
    actual fun property(path: String): FileProperty? {
        val file = File(path)

        return buildFileProperty(file)
    }

    /**
     * Returns stats for the resource at `pathComponent`.
     */
    actual fun property(pathComponent: PathComponent): FileProperty? {
        val path = pathComponent.component ?: return null

        return buildFileProperty(File(path))
    }

    /**
     *
     * Returns the contents of the file located at `path`. The content is parsed according to `encoding`.
     * For binary files, use `encoding` = [ContentEncoding.BASE_64].
     *
     */
    actual fun readFile(
        path: String,
        encoding: ContentEncoding
    ): String? {
        val file = File(path).canonicalFile

        val charset = when (encoding) {
            ContentEncoding.ASCII -> Charsets.US_ASCII
            else -> Charsets.UTF_8
        }
        val reader = BufferedReader(InputStreamReader(FileInputStream(file), charset))
        val content = reader.readLines().joinToString("\n")

        if (encoding == ContentEncoding.BASE_64) {
            return String(Base64.decode(content, Base64.DEFAULT), Charsets.UTF_8)
        }
        return content
    }

    /**
     *
     * Returns the contents of the file located at `pathComponent`. The content is parsed according to `encoding`.
     * For binary files, use `encoding` = [ContentEncoding.Base64].
     *
     */
    actual fun readFile(
        pathComponent: PathComponent,
        encoding: ContentEncoding
    ): String? {
        val path = pathComponent.component ?: return null

        return readFile(path, encoding)
    }

    /**
     * Returns the contents of the file located at `path` as ByteArray.
     */
    actual fun readFileAsByteArray(path: String): ByteArray? {
        val file = File(path).canonicalFile

        return file.readBytes()
    }

    /**
     * Returns the contents of the file located at `pathComponent` as ByteArray.
     */
    actual fun readFileAsByteArray(pathComponent: PathComponent): ByteArray? {
        val path = pathComponent.component ?: return null

        return readFileAsByteArray(path)
    }

    /**
     * Writes `contents` to the file located at `path`. If `create` is true, then file is created if it does not exist.
     * For binary files, use `encoding` = [ContentEncoding.BASE_64].
     * * Returns true if operation is successful, otherwise false.
     */
    actual fun writeFile(
        path: String,
        contents: String,
        create: Boolean,
        encoding: ContentEncoding,
        append: Boolean
    ): Boolean {
        val file = File(path).canonicalFile
        if (!file.exists()) {
            if (!create) return false
            file.createNewFile()
        }

        val finalContent: String
        val appendToFile: Boolean

        if (encoding == ContentEncoding.BASE_64) {
            val sourceString =
                if (append) (readFile(path, ContentEncoding.BASE_64) ?: "") + contents else contents

            finalContent =
                Base64.encodeToString(sourceString.toByteArray(Charsets.UTF_8), Base64.DEFAULT)
            appendToFile = false

        } else {
            finalContent = contents
            appendToFile = append
        }

        val charset = when (encoding) {
            ContentEncoding.ASCII -> Charsets.US_ASCII
            else -> Charsets.UTF_8
        }
        val bufferedWriter =
            BufferedWriter(OutputStreamWriter(FileOutputStream(file, appendToFile), charset))
        bufferedWriter.write(finalContent)
        bufferedWriter.close()
        return true
    }

    /**
     * Writes `contents` to the file located at `pathComponent`.
     * If `create` is true, then file is created if it does not exist.
     * For binary files, use `encoding` = [ContentEncoding.Base64].
     * Returns true if operation is successful, otherwise false.
     */
    actual fun writeFile(
        pathComponent: PathComponent,
        contents: String,
        create: Boolean,
        encoding: ContentEncoding,
        append: Boolean
    ): Boolean {
        val path = pathComponent.component ?: return false

        return writeFile(path, contents, create, encoding, append)
    }

    /**
     * Writes `contents` to the file located at `path`. If `create` is true, then file is created if it does not exist.
     * Returns true if operation is successful, otherwise false.
     */
    actual fun writeFile(
        path: String,
        contents: ByteArray,
        create: Boolean,
        append: Boolean
    ): Boolean {
        val file = File(path).canonicalFile
        if (!file.exists()) {
            if (!create) return false
            file.createNewFile()
        }

        FileOutputStream(file, append).write(contents)
        return true
    }

    /**
     * Writes `contents` to the file located at `pathComponent`. If `create` is true, then file is created if it does not exist.
     * Returns true if operation is successful, otherwise false.
     */
    actual fun writeFile(
        pathComponent: PathComponent,
        contents: ByteArray,
        create: Boolean,
        append: Boolean
    ): Boolean {
        val path = pathComponent.component ?: return false

        return writeFile(path, contents, create, append)
    }

    /**
     * Creates a file at `path` if it does not exist.
     * Returns false if file already exists, otherwise true.
     */
    actual fun touch(path: String): Boolean {
        val file = File(path).canonicalFile
        return file.createNewFile()
    }

    /**
     * Creates a file at `pathComponent` if it does not exist.
     * Returns false if file already exists, otherwise true.
     */
    actual fun touch(pathComponent: PathComponent): Boolean {
        val path = pathComponent.component ?: return false
        return touch(path)
    }

    /**
     * Creates a directory on `path`.
     * If `recursive` is true, then intermediate directories are also created.
     * Returns true if directory is created successfully.
     */
    actual fun mkdir(path: String, recursive: Boolean): Boolean {
        val file = File(path).canonicalFile
        return if (recursive) file.mkdirs() else file.mkdir()
    }

    /**
     * Creates a directory on `pathComponent`.
     * If `recursive` is true, then intermediate directories are also created.
     * Returns true if directory is created successfully.
     */
    actual fun mkdir(
        pathComponent: PathComponent,
        recursive: Boolean
    ): Boolean {
        val path = pathComponent.component ?: return false

        return mkdir(path, recursive)
    }

    /**
     * Returns true if the file or directory exists at `path`.
     */
    actual fun exists(path: String): Boolean {
        val file = File(path).canonicalFile

        return file.exists()
    }

    /**
     * Returns true if the file or directory exists at `pathComponent`.
     */
    actual fun exists(pathComponent: PathComponent): Boolean {
        val path = pathComponent.component ?: return false

        return exists(path)
    }

    /**
     * Removes a file on `path`.
     * If it is a directory, its contents are removed as well.
     * Returns true if file is deleted successfully, otherwise false.
     */
    actual fun remove(path: String): Boolean {
        return File(path).canonicalFile.deleteRecursively()
    }

    /**
     * Removes a file on `pathComponent`.
     * If it is a directory, its contents are removed as well.
     * Returns true if file is deleted successfully, otherwise false.
     */
    actual fun remove(pathComponent: PathComponent): Boolean {
        val path = pathComponent.component ?: return false

        return remove(path)
    }

    /**
     * Moves the file from `srcPath` to `destPath`.
     * If `srcPath` is a directory, its contents including hidden files are moved.
     * Returns true if the move is successful, otherwise false.
     */
    actual fun moveFile(srcPath: String, destPath: String): Boolean {
        val srcFile = File(srcPath).canonicalFile
        val destFile = File(destPath).canonicalFile
        return srcFile.renameTo(destFile)
    }

    /**
     * Moves the file from `srcPathComponent` to `destPathComponent`.
     * If `srcPathComponent` is a directory, its contents including hidden files are moved.
     * Returns true if the move is successful, otherwise false.
     */
    actual fun moveFile(
        srcPathComponent: PathComponent,
        destPathComponent: PathComponent
    ): Boolean {
        val srcPath = srcPathComponent.component ?: return false
        val destPath = destPathComponent.component ?: return false

        return moveFile(srcPath, destPath)
    }

    /**
     * Copies the file from `srcPath` to `destPath`.
     * If `srcPath` is a directory, its contents including hidden files are copied.
     * Returns true if the copy is successful, otherwise false.
     */
    actual fun copyFile(srcPath: String, destPath: String): Boolean {
        val srcFile = File(srcPath).canonicalFile
        val destFile = File(destPath).canonicalFile
        return srcFile.copyRecursively(destFile)
    }

    /**
     * Copies the file from `srcPathComponent` to `destPathComponent`.
     * If `srcPathComponent` is a directory, its contents including hidden files are copied.
     * Returns true if the copy is successful, otherwise false.
     */
    actual fun copyFile(
        srcPathComponent: PathComponent,
        destPathComponent: PathComponent
    ): Boolean {
        val srcPath = srcPathComponent.component ?: return false
        val destPath = destPathComponent.component ?: return false

        return copyFile(srcPath, destPath)
    }

    private inline fun <T, U> executeIfNotNull(what: U?, execute: (what: U) -> Unit): T {
        what?.let {
            execute.invoke(it)
        }
        throw NullPointerException("Params is null: $what")
    }

    private fun buildFileProperty(file: File): FileProperty {
        val fileType = when {
            file.isDirectory -> FileType.Directory
            file.isFile -> FileType.File
            else -> FileType.Unknown
        }
        return FileProperty(
            name = file.name,
            extension = file.name.split(".").last(),
            absolutePath = PathComponent(file.absolutePath),
            canonicalPath = PathComponent(file.canonicalPath),
            createdAt = 0,
            modifiedAt = file.lastModified(),
            size = file.length(),
            type = fileType
        )
    }
}