@file:Suppress("unused")

package id.indosw.markdownviewlib.css

import java.io.File
import java.net.URL

class ExternalStyleSheet(val url: String) : StyleSheet {
    private var mediaQuery: String? = null

    constructor(url: String, mediaQuery: String?) : this(url) {
        this.mediaQuery = mediaQuery
    }

    override fun toString(): String {
        return url
    }

    override fun toHTML(): String {
        return String.format(
            "<link rel=\"stylesheet\" type=\"text/css\" media=\"%s\" href=\"%s\" />\n",
            if (mediaQuery == null) "" else mediaQuery,
            url
        )
    }

    companion object {
        fun fromUrl(url: URL, mediaQuery: String?): ExternalStyleSheet {
            return ExternalStyleSheet(url.toString(), mediaQuery)
        }

        fun fromFile(file: File, mediaQuery: String?): ExternalStyleSheet {
            return ExternalStyleSheet(file.absolutePath, mediaQuery)
        }

        fun fromAsset(path: String, mediaQuery: String?): StyleSheet {
            return ExternalStyleSheet("file:///android_asset/$path", mediaQuery)
        }
    }
}