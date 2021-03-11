package id.indosw.markdownviewlib.ext.label.internal

import com.vladsch.flexmark.html.HtmlWriter
import com.vladsch.flexmark.html.renderer.NodeRenderer
import com.vladsch.flexmark.html.renderer.NodeRendererContext
import com.vladsch.flexmark.html.renderer.NodeRendererFactory
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler
import com.vladsch.flexmark.util.options.DataHolder
import id.indosw.markdownviewlib.ext.label.Label
import java.util.*

class LabelNodeRenderer(options: DataHolder?) : NodeRenderer {
    override fun getNodeRenderingHandlers(): Set<NodeRenderingHandler<*>> {
        val set = HashSet<NodeRenderingHandler<*>>()
        set.add(NodeRenderingHandler(Label::class.java) { node: Label, context: NodeRendererContext, html: HtmlWriter ->
            render(
                node,
                context,
                html
            )
        })
        return set
    }

    private fun render(node: Label, context: NodeRendererContext, html: HtmlWriter) {
        when (node.type) {
            3 -> html.attr(
                "class",
                "lbl-success"
            )
            4 -> html.attr(
                "class",
                "lbl-warning"
            )
            5 -> html.attr("class", "lbl-danger")
        }
        html.withAttr().tag("lbl")
        context.renderChildren(node)
        html.tag("/lbl")
    }

    class Factory : NodeRendererFactory {
        override fun create(options: DataHolder): NodeRenderer {
            return LabelNodeRenderer(options)
        }
    }
}