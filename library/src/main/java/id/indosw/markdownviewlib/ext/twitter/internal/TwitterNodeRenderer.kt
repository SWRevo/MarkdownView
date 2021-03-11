package id.indosw.markdownviewlib.ext.twitter.internal

import android.os.ConditionVariable
import android.text.TextUtils
import com.vladsch.flexmark.html.HtmlWriter
import com.vladsch.flexmark.html.renderer.*
import com.vladsch.flexmark.util.options.DataHolder
import id.indosw.markdownviewlib.Utils.getStringFromInputStream
import id.indosw.markdownviewlib.ext.twitter.Twitter
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.io.UnsupportedEncodingException
import java.net.URL
import java.net.URLEncoder
import java.util.*

class TwitterNodeRenderer(options: DataHolder?) : NodeRenderer {
    private val mCondition = ConditionVariable(false)
    private var mHtml: String? = null
    override fun getNodeRenderingHandlers(): Set<NodeRenderingHandler<*>> {
        val set = HashSet<NodeRenderingHandler<*>>()
        set.add(NodeRenderingHandler(Twitter::class.java) { node: Twitter, context: NodeRendererContext, html: HtmlWriter ->
            render(
                node,
                context,
                html
            )
        })
        return set
    }

    private fun render(node: Twitter, context: NodeRendererContext, html: HtmlWriter) {
        val name = node.text.toString()
        if (context.isDoNotRenderLinks) {
            context.renderChildren(node)
        } else if (!TextUtils.isEmpty(name)) {
            val url: String
            val value: String = try {
                URLEncoder.encode(
                    context.resolveLink(LinkType.LINK, node.url.unescape(), null).url,
                    "utf-8"
                )
            } catch (e: UnsupportedEncodingException) {
                context.renderChildren(node)
                return
            }
            when (name) {
                "tweet" -> url = String.format(
                    "https://publish.twitter.com/oembed?url=https://twitter.com/twitter/status/%s",
                    value
                )
                "tweet-hide-cards" -> url = String.format(
                    "https://publish.twitter.com/oembed?url=https://twitter.com/twitter/status/%s&hide_media=true",
                    value
                )
                "follow" -> {
                    mHtml = String.format(
                        "<a href=\"https://twitter.com/%s\" data-size=\"large\" class=\"twitter-follow-button\" data-show-count=\"true\">Follow @%s</a><script async src=\"https://platform.twitter.com/widgets.js\" charset=\"utf-8\"></script>",
                        value, value
                    )
                    html.srcPos(node.chars).append(mHtml)
                    return
                }
                else -> {
                    context.renderChildren(node)
                    return
                }
            }
            mHtml = null
            Thread(LoadTweetRunnable(url)).start()
            mCondition.close()
            mCondition.block()
            if (mHtml == null) {
                context.renderChildren(node)
            } else {
                mHtml = mHtml!!.replace("src=\"//".toRegex(), "src=\"https://")
                html.srcPos(node.chars).append(mHtml)
            }
        }
    }

    class Factory : NodeRendererFactory {
        override fun create(options: DataHolder): NodeRenderer {
            return TwitterNodeRenderer(options)
        }
    }

    private inner class LoadTweetRunnable(private val mUrl: String) : Runnable {
        override fun run() {
            var `is`: InputStream? = null
            try {
                val connection = URL(mUrl).openConnection()
                connection.readTimeout = 5000
                connection.connectTimeout = 5000
                connection.setRequestProperty("Accept-Charset", "UTF-8")
                val json = getStringFromInputStream(connection.getInputStream().also { `is` = it })
                mHtml = if (!TextUtils.isEmpty(json)) {
                    val tweet = JSONObject(json)
                    tweet.getString("html")
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (`is` != null) {
                    try {
                        `is`!!.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                mCondition.open()
            }
        }
    }
}