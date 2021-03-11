package id.indosw.markdownviewlib.ext.emoji.internal

import com.vladsch.flexmark.html.HtmlWriter
import com.vladsch.flexmark.html.renderer.*
import com.vladsch.flexmark.util.options.DataHolder
import id.indosw.markdownviewlib.ext.emoji.Emoji
import id.indosw.markdownviewlib.ext.emoji.EmojiExtension
import java.util.*

class EmojiNodeRenderer(options: DataHolder) : NodeRenderer {
    private val rootImagePath: String = options.get(EmojiExtension.ROOT_IMAGE_PATH)
    private val attrImageSize: String = options.get(EmojiExtension.ATTR_IMAGE_SIZE)
    private val attrAlign: String = options.get(EmojiExtension.ATTR_ALIGN)
    private val extImage: String = options.get(EmojiExtension.IMAGE_EXT)
    override fun getNodeRenderingHandlers(): Set<NodeRenderingHandler<*>> {
        val set = HashSet<NodeRenderingHandler<*>>()
        set.add(NodeRenderingHandler(Emoji::class.java) { node: Emoji, context: NodeRendererContext, html: HtmlWriter ->
            render(
                node,
                context,
                html
            )
        })
        return set
    }

    private fun render(node: Emoji, context: NodeRendererContext, html: HtmlWriter) {
        val shortcut = EmojiCheatSheet.getImageShortcut(node.text.toString())
        if (shortcut == null) {
            // output as text
            html.text(":")
            context.renderChildren(node)
            html.text(":")
        } else {
            val resolvedLink = context.resolveLink(
                LinkType.LINK,
                rootImagePath + shortcut.image + "." + extImage,
                null
            )
            html.attr("src", resolvedLink.url)
            html.attr("alt", "emoji " + shortcut.category + ":" + shortcut.name)
            if (attrImageSize.isNotEmpty()) html.attr("height", attrImageSize)
                .attr("width", attrImageSize)
            if (attrAlign.isNotEmpty()) html.attr("align", attrAlign)
            html.withAttr(resolvedLink)
            html.tagVoid("img")
        }
    }

    class Factory : NodeRendererFactory {
        override fun create(options: DataHolder): NodeRenderer {
            return EmojiNodeRenderer(options)
        }
    }

}