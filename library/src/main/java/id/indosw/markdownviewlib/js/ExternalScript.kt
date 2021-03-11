@file:Suppress("SpellCheckingInspection")

package id.indosw.markdownviewlib.js

class ExternalScript(
    val src: String?,
    val isAync: Boolean,
    val isDefer: Boolean,
    val type: String
) : JavaScript {

    constructor(url: String?, isAync: Boolean, isDefer: Boolean) : this(
        url,
        isAync,
        isDefer,
        "text/javascript"
    )

    override fun toHTML(): String {
        return String.format(
            "<script %s%ssrc='%s' type='%s'></script>\n",
            if (isAync) "async " else "",
            if (isDefer) "defer " else "",
            src,
            type
        )
    }
}