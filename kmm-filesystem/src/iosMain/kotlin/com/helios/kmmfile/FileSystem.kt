package com.helios.kmmfile

import com.helios.kmmfile.util.Utils
import com.helios.kmmfile.util.Utils.fromData
import com.helios.kmmfile.util.Utils.toByteArray
import com.helios.kmmfile.util.Utils.toData
import com.helios.kmmfile.util.Utils.toNSData
import kotlinx.cinterop.*
import platform.Foundation.*

/**
 * Created by phpduy99 on 11/13/2022
 */
actual object FileSystem {

    private val manager = NSFileManager.defaultManager

    /**
     * Path to the common contents directory. Directory is guaranteed to exist (created if not exists already).
     *
     * On Android, it points to `files` directory.
     * On iOS, it points to `Documents` directory.
     */
    actual val contentsDirectory: Path
        get() = getDirPathInternal(NSDocumentDirectory, true)

    /**
     * Path to caches directory. Directory is guaranteed to exist (created if not exists already).
     */
    actual val cachesDirectory: Path
        get() = getDirPathInternal(NSCachesDirectory, true)

    /**
     * Path to Temporary directory. Directory is guaranteed to exist (created if not exists already).
     */
    actual val temporaryDirectory: Path
        get() = Path.fromUrlString(NSTemporaryDirectory())

    /**
     * Returns a list of stats for the contents of directory at `path`.
     */
    actual fun readDir(path: String): List<FileProperty>? {
        return readDirInternal(urlFromString(path))
    }

    /**
     * Returns a list of stats for the contents of directory at `pathComponent`.
     */
    actual fun readDir(pathComponent: PathComponent): List<FileProperty>? {
        return readDirInternal(pathComponent.url)
    }

    /**
     * Returns stats for the resource at [path].
     */
    actual fun property(path: String): FileProperty? {
        return buildPropertyInternal(urlFromString(path))
    }

    /**
     * Returns stats for the resource at `pathComponent`.
     */
    actual fun property(pathComponent: PathComponent): FileProperty? {
        return buildPropertyInternal(pathComponent.url)
    }

    /**
     *
     * Returns the contents of the file located at `path`. The content is parsed according to `encoding`.
     * For binary files, use `encoding` = [ContentEncoding.Base64].
     *
     */
    actual fun readFile(
        path: String,
        encoding: ContentEncoding
    ): String? {
        return readFileInternal(urlFromString(path), encoding)
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
        return readFileInternal(pathComponent.url, encoding)
    }

    /**
     * Returns the contents of the file located at `path` as ByteArray.
     */
    actual fun readFileAsByteArray(path: String): ByteArray? {
        val url = urlFromString(path)
        val pathFromUrl = url?.standardizedURL?.path ?: return null
        val data = manager.contentsAtPath(pathFromUrl) ?: return null

        return data.toByteArray()
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
     * For binary files, use `encoding` = [ContentEncoding.Base64].
     * * Returns true if operation is successful, otherwise false.
     */
    actual fun writeFile(
        path: String,
        contents: String,
        create: Boolean,
        encoding: ContentEncoding,
        append: Boolean
    ): Boolean {
        return if (append) {
            appendFileInternal(urlFromString(path), contents, create, encoding)
        } else {
            writeFile(urlFromString(path), contents, create, encoding)
        }
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
        return if (append) {
            appendFileInternal(pathComponent.url, contents, create, encoding)
        } else {
            writeFile(pathComponent.url, contents, create, encoding)
        }
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
        if (append) {
            return appendFileInternal(urlFromString(path), contents, create)
        } else {
            val pathStandardized = urlFromString(path)?.standardizedURL?.path ?: return false

            if (!ensureFileExists(pathStandardized, create, false)) {
                return false
            }

            val data = contents.toNSData()
            return data.writeToFile(pathStandardized, true)
        }
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
        if (append) {
            return appendFileInternal(pathComponent.url, contents, create)
        } else {
            val path = pathComponent.component ?: return false
            return writeFile(path, contents, create, append)
        }
    }

    /**
     * Creates a file at `path` if it does not exist.
     * Returns false if file already exists, otherwise true.
     */
    actual fun touch(path: String): Boolean {
        return newFileInternal(urlFromString(path))
    }

    /**
     * Creates a file at `pathComponent` if it does not exist.
     * Returns false if file already exists, otherwise true.
     */
    actual fun touch(pathComponent: PathComponent): Boolean {
        return newFileInternal(pathComponent.url)
    }

    /**
     * Creates a directory on `path`.
     * If `recursive` is true, then intermediate directories are also created.
     * Returns true if directory is created successfully.
     */
    actual fun mkdir(path: String, recursive: Boolean): Boolean {
        return mkdirInternal(urlFromString(path), recursive)
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
        return mkdirInternal(pathComponent.url, recursive)
    }

    /**
     * Returns true if the file or directory exists at `path`.
     */
    actual fun exists(path: String): Boolean {
        return existsInternal(urlFromString(path))
    }

    /**
     * Returns true if the file or directory exists at `pathComponent`.
     */
    actual fun exists(pathComponent: PathComponent): Boolean {
        return existsInternal(pathComponent.url)
    }

    /**
     * Removes a file on `path`.
     * If it is a directory, its contents are removed as well.
     * Returns true if file is deleted successfully, otherwise false.
     */
    actual fun remove(path: String): Boolean {
        return removeInternal(urlFromString(path))
    }

    /**
     * Removes a file on `pathComponent`.
     * If it is a directory, its contents are removed as well.
     * Returns true if file is deleted successfully, otherwise false.
     */
    actual fun remove(pathComponent: PathComponent): Boolean {
        return removeInternal(pathComponent.url)
    }

    /**
     * Moves the file from `srcPath` to `destPath`.
     * If `srcPath` is a directory, its contents including hidden files are moved.
     * Returns true if the move is successful, otherwise false.
     */
    actual fun moveFile(srcPath: String, destPath: String): Boolean {
        return moveFileInternal(urlFromString(srcPath), urlFromString(destPath))
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
        return moveFileInternal(srcPathComponent.url, destPathComponent.url)
    }

    /**
     * Copies the file from `srcPath` to `destPath`.
     * If `srcPath` is a directory, its contents including hidden files are copied.
     * Returns true if the copy is successful, otherwise false.
     */
    actual fun copyFile(srcPath: String, destPath: String): Boolean {
        return copyFileInternal(urlFromString(srcPath), urlFromString(destPath))
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
        return copyFileInternal(srcPathComponent.url, destPathComponent.url)
    }

    private fun getDirUrl(directory: NSSearchPathDirectory, create: Boolean = false): NSURL? {
        memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            return manager.URLForDirectory(
                directory,
                NSUserDomainMask,
                null,
                create,
                error.ptr
            )?.standardizedURL
        }
    }

    fun getDirPathInternal(directory: NSSearchPathDirectory, create: Boolean = false): Path {
        return Path.fromUrl(getDirUrl(directory, create))
    }

    private fun attributesOfFile(filePath: String): Map<String, Any?> {
        memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            val attributes =
                manager.attributesOfItemAtPath(filePath, error.ptr) ?: return emptyMap()
            val result = mutableMapOf<String, Any?>()
            for ((key, value) in attributes) {
                if (key != null && key is String) {
                    result[key] = value
                }
            }
            return result
        }
    }

    private fun fileExists(path: String, isDirectory: Boolean = false): Boolean {
        memScoped {
            val boolean = alloc<BooleanVar>()
            boolean.value = isDirectory
            return manager.fileExistsAtPath(path, isDirectory = boolean.ptr)
        }
    }

    private fun existsInternal(url: NSURL?): Boolean {
        val path = url?.standardizedURL?.path ?: return false
        return manager.fileExistsAtPath(path)
    }

    private fun readDirInternal(url: NSURL?): List<FileProperty>? {
        val stdPath = url?.standardizedURL?.path ?: return null
        memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            val contents =
                manager.contentsOfDirectoryAtPath(stdPath, error.ptr) ?: return emptyList()
            return contents.mapNotNull {
                buildPropertyInternal(url.URLByAppendingPathComponent(it as String) ?: return null)
            }
        }
    }

    private fun buildPropertyInternal(url: NSURL?): FileProperty? {
        val stdPath = url?.standardizedURL?.path ?: return null
        val attributes = attributesOfFile(stdPath)
        val createdAt = Utils.toInterval(attributes[NSFileCreationDate])
        val modifiedAt = Utils.toInterval(attributes[NSFileModificationDate])
        val size = attributes[NSFileSize] as? Double
        val type = when (attributes[NSFileType] as? String) {
            NSFileTypeRegular -> FileType.File
            NSFileTypeDirectory -> FileType.Directory
            else -> FileType.Unknown
        }

        return FileProperty(
            name = url.lastPathComponent ?: "",
            extension = url.lastPathComponent?.split(".")?.last(),
            canonicalPath = PathComponent(stdPath),
            absolutePath = PathComponent(stdPath),
            createdAt = createdAt?.toLong(),
            modifiedAt = modifiedAt?.toLong(),
            size = size?.toLong(),
            type = type
        )
    }

    private fun urlFromString(urlString: String?): NSURL? {
        return if (urlString != null) NSURL.fileURLWithPath(urlString).standardizedURL else null
    }

    private fun readFileInternal(url: NSURL?, encoding: ContentEncoding): String? {
        val path = url?.standardizedURL?.path ?: return null
        val data = manager.contentsAtPath(path) ?: return null
        return String.fromData(data, encoding)

    }

    private fun writeFile(
        url: NSURL?,
        contents: String,
        create: Boolean,
        encoding: ContentEncoding
    ): Boolean {
        val path = url?.standardizedURL?.path ?: return false

        if (!ensureFileExists(path, create, false))
            return false

        val data = contents.toData(encoding) ?: return false
        return data.writeToFile(path, true)
    }

    private fun ensureFileExists(
        path: String,
        create: Boolean = false,
        isDirectory: Boolean = false
    ): Boolean {
        val exists = fileExists(path, isDirectory)
        if (!exists && !create) return false

        if (!exists && create) {
            return if (isDirectory) {
                manager.createDirectoryAtPath(path, emptyMap<Any?, Any>())
            } else {
                manager.createFileAtPath(path, null, emptyMap<Any?, Any>())
            }
        }
        return true
    }

    private fun appendFileInternal(
        url: NSURL?,
        contents: String,
        create: Boolean,
        encoding: ContentEncoding
    ): Boolean {
        val path = url?.standardizedURL?.path ?: return false

        if (encoding == ContentEncoding.BASE_64) {
            val existingContents = readFileInternal(url, ContentEncoding.BASE_64) ?: ""
            return writeFile(
                url,
                (existingContents + contents),
                create = create,
                encoding = ContentEncoding.BASE_64
            )
        }

        val data = contents.toData(encoding) ?: return false
        if (!ensureFileExists(path, create, false)) return false

        val handle = NSFileHandle.fileHandleForUpdatingAtPath(path) ?: return false
        handle.seekToEndOfFile()
        handle.writeData(data)
        handle.closeFile()
        return true
    }

    private fun appendFileInternal(
        url: NSURL?,
        contents: ByteArray,
        create: Boolean,
    ): Boolean {
        val path = url?.standardizedURL?.path ?: return false

        val data = contents.toNSData()
        if (!ensureFileExists(path, create, false)) return false

        val handle = NSFileHandle.fileHandleForUpdatingAtPath(path) ?: return false
        handle.seekToEndOfFile()
        handle.writeData(data)
        handle.closeFile()
        return true
    }

    private fun newFileInternal(url: NSURL?): Boolean {
        val path = url?.standardizedURL?.path ?: return false
        if (fileExists(path, false)) return false
        return manager.createFileAtPath(path, null, null)
    }

    private fun mkdirInternal(url: NSURL?, recursive: Boolean): Boolean {
        val path = url?.standardizedURL?.path ?: return false
        if (ensureFileExists(path, create = false, isDirectory = true)) return false
        memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            return manager.createDirectoryAtPath(path, recursive, emptyMap<Any?, Any>(), error.ptr)
        }
    }

    private fun removeInternal(url: NSURL?): Boolean {
        val path = url?.standardizedURL?.path ?: return false
        memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            return manager.removeItemAtPath(path, error.ptr)
        }
    }

    private fun moveFileInternal(srcUrl: NSURL?, destUrl: NSURL?): Boolean {
        val srcPath = srcUrl?.standardizedURL?.path ?: return false
        val destPath = destUrl?.standardizedURL?.path ?: return false
        memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            return manager.moveItemAtPath(srcPath, destPath, error.ptr)
        }
    }

    private fun copyFileInternal(srcUrl: NSURL?, destUrl: NSURL?): Boolean {
        val srcPath = srcUrl?.standardizedURL?.path ?: return false
        val destPath = destUrl?.standardizedURL?.path ?: return false
        memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            return manager.copyItemAtPath(srcPath, destPath, error.ptr)
        }
    }
}
