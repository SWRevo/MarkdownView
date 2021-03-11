package id.indosw.markdownviewlib

import kotlin.Throws
import android.content.res.AssetManager
import java.io.*
import java.lang.Exception

object Utils {
    @JvmStatic
    @Throws(IOException::class)
    fun getStringFromInputStream(`is`: InputStream): String {
        val baos = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var length: Int
        while (`is`.read(buffer).also { length = it } != -1) {
            baos.write(buffer, 0, length)
        }
        return baos.toString("UTF-8")
    }

    @JvmStatic
    fun getStringFromAssetFile(asset: AssetManager, filename: String?): String {
        var `is`: InputStream? = null
        return try {
            `is` = asset.open(filename!!)
            getStringFromInputStream(`is`)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        } finally {
            if (`is` != null) {
                try {
                    `is`.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    @JvmStatic
    fun getStringFromFile(file: File?): String {
        var `is`: InputStream? = null
        return try {
            `is` = FileInputStream(file)
            getStringFromInputStream(`is`)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        } finally {
            if (`is` != null) {
                try {
                    `is`.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}