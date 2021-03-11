package id.indosw.markdownviewlib.ext.mathjax.internal

import com.vladsch.flexmark.html.HtmlWriter
import com.vladsch.flexmark.html.renderer.NodeRenderer
import com.vladsch.flexmark.html.renderer.NodeRendererContext
import com.vladsch.flexmark.html.renderer.NodeRendererFactory
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler
import com.vladsch.flexmark.util.options.DataHolder
import id.indosw.markdownviewlib.ext.mathjax.MathJax
import java.util.*

class MathJaxNodeRenderer(options: DataHolder?) : NodeRenderer {
    override fun getNodeRenderingHandlers(): Set<NodeRenderingHandler<*>> {
        val set = HashSet<NodeRenderingHandler<*>>()
        set.add(NodeRenderingHandler(MathJax::class.java) { node: MathJax, context: NodeRendererContext, html: HtmlWriter ->
            render(
                node,
                context,
                html
            )
        })
        return set
    }

    private fun render(node: MathJax, context: NodeRendererContext, html: HtmlWriter) {
        html.withAttr().attr("class", "math").tag("span")
        if (node.isInline) {
            html.append("\\(")
        } else {
            html.append("$$")
        }
        context.renderChildren(node)
        if (node.isInline) {
            html.append("\\)")
        } else {
            html.append("$$")
        }
        html.tag("/span")
    }

    class Factory : NodeRendererFactory {
        override fun create(options: DataHolder): NodeRenderer {
            return MathJaxNodeRenderer(options)
        }
    }
}