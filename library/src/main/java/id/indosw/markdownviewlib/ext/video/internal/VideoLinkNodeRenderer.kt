package id.indosw.markdownviewlib.ext.video.internal

import android.text.TextUtils
import com.vladsch.flexmark.html.HtmlWriter
import com.vladsch.flexmark.html.renderer.*
import com.vladsch.flexmark.util.options.DataHolder
import id.indosw.markdownviewlib.ext.video.VideoLink
import java.util.*

class VideoLinkNodeRenderer(options: DataHolder?) : NodeRenderer {
    override fun getNodeRenderingHandlers(): Set<NodeRenderingHandler<*>> {
        val set = HashSet<NodeRenderingHandler<*>>()
        set.add(NodeRenderingHandler(VideoLink::class.java) { node: VideoLink, context: NodeRendererContext, html: HtmlWriter ->
            render(
                node,
                context,
                html
            )
        })
        return set
    }

    private fun render(node: VideoLink, context: NodeRendererContext, html: HtmlWriter) {
        val name = node.text.toString()
        if (context.isDoNotRenderLinks) {
            context.renderChildren(node)
        } else if (!TextUtils.isEmpty(name)) {
            val resolvedLink = context.resolveLink(LinkType.LINK, node.url.unescape(), null)
            if (name == "youtube" || name == "yt") {
                html.attr("class", "player yt-player")
                html.withAttr().tag("div")
                html.attr("type", "text/html")
                html.attr("frameborder", "0")
                html.attr("allowfullscreen", "")
                html.attr(
                    "src",
                    String.format("https://www.youtube.com/embed/%s", resolvedLink.url)
                )
                html.srcPos(node.chars).withAttr(resolvedLink).tag("iframe")
                html.tag("/iframe")
                html.tag("/div")
            } else {
                context.renderChildren(node)
            }
        }
    }

    class Factory : NodeRendererFactory {
        override fun create(options: DataHolder): NodeRenderer {
            return VideoLinkNodeRenderer(options)
        }
    }
}