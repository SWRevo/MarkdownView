package id.indosw.markdownviewlib.ext.mark.internal

import com.vladsch.flexmark.html.HtmlWriter
import com.vladsch.flexmark.html.renderer.NodeRenderer
import com.vladsch.flexmark.html.renderer.NodeRendererContext
import com.vladsch.flexmark.html.renderer.NodeRendererFactory
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler
import com.vladsch.flexmark.util.options.DataHolder
import id.indosw.markdownviewlib.ext.mark.Mark
import java.util.*

class MarkNodeRenderer(options: DataHolder?) : NodeRenderer {
    override fun getNodeRenderingHandlers(): Set<NodeRenderingHandler<*>> {
        val set = HashSet<NodeRenderingHandler<*>>()
        set.add(NodeRenderingHandler(Mark::class.java) { node: Mark, context: NodeRendererContext, html: HtmlWriter ->
            render(
                node,
                context,
                html
            )
        })
        return set
    }

    private fun render(node: Mark, context: NodeRendererContext, html: HtmlWriter) {
        html.srcPos(node.text).withAttr().tag("mark")
        context.renderChildren(node)
        html.tag("/mark")
    }

    class Factory : NodeRendererFactory {
        override fun create(options: DataHolder): NodeRenderer {
            return MarkNodeRenderer(options)
        }
    }
}