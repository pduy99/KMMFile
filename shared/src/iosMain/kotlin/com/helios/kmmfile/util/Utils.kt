package com.helios.kmmfile.util

import com.helios.kmmfile.ContentEncoding
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.*
import platform.posix.memcpy

/**
 * Created by phpduy99 on 11/13/2022
 */
object Utils {

    fun NSData.toByteArray(): ByteArray {
        return ByteArray(this@toByteArray.length.toInt()).apply {
            usePinned {
                memcpy(it.addressOf(0), this@toByteArray.bytes, this@toByteArray.length)
            }
        }
    }

    fun toInterval(input: Any?): Double? {
        if (input == null || input !is NSDate) return null
        return input.timeIntervalSince1970()
    }

    fun ByteArray.toNSData(): NSData {
        if (isEmpty()) {
            NSData()
        }

        return usePinned {
            return@usePinned NSData.dataWithBytes(it.addressOf(0), it.get().size.toULong())
        }
    }

    val String.nativeStr: NSString
        get() = NSString.create(string = this)

    fun String.toData(encoding: ContentEncoding): NSData? {
        return when (encoding) {
            ContentEncoding.BASE_64 -> nativeStr.dataUsingEncoding(NSUTF8StringEncoding)
                ?.base64EncodedDataWithOptions(NSDataBase64EncodingEndLineWithCarriageReturn)
            ContentEncoding.ASCII -> nativeStr.dataUsingEncoding(NSASCIIStringEncoding)
            ContentEncoding.UTF_8 -> nativeStr.dataUsingEncoding(NSUTF8StringEncoding)
        }
    }

    fun String.Companion.fromData(data: NSData, encoding: ContentEncoding): String? {
        return when (encoding) {
            ContentEncoding.UTF_8 -> NSString.create(data, NSUTF8StringEncoding).toString()
            ContentEncoding.ASCII -> NSString.create(data, NSASCIIStringEncoding).toString()
            ContentEncoding.BASE_64 -> {
                val base64String =
                    NSString.create(data = data, encoding = NSUTF8StringEncoding)?.toString()
                        ?: return null

                return base64String.split("\n")
                    .filter { it.isNotEmpty() }
                    .mapNotNull {
                        NSData.create(it, NSDataBase64EncodingEndLineWithCarriageReturn)
                    }.mapNotNull {
                        String.fromData(it, ContentEncoding.UTF_8)
                    }.joinToString("\n")
            }

        }
    }
}