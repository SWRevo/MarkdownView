package id.indosw.markdownviewlib.ext.kbd.internal

import com.vladsch.flexmark.html.HtmlWriter
import com.vladsch.flexmark.html.renderer.NodeRenderer
import com.vladsch.flexmark.html.renderer.NodeRendererContext
import com.vladsch.flexmark.html.renderer.NodeRendererFactory
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler
import com.vladsch.flexmark.util.options.DataHolder
import id.indosw.markdownviewlib.ext.kbd.Keystroke
import java.util.*

class KeystrokeNodeRenderer(options: DataHolder?) : NodeRenderer {
    override fun getNodeRenderingHandlers(): Set<NodeRenderingHandler<*>> {
        val set = HashSet<NodeRenderingHandler<*>>()
        set.add(NodeRenderingHandler(Keystroke::class.java) { node: Keystroke, context: NodeRendererContext, html: HtmlWriter ->
            render(
                node,
                context,
                html
            )
        })
        return set
    }

    private fun render(node: Keystroke, context: NodeRendererContext, html: HtmlWriter) {
        html.withAttr().tag("kbd")
        html.append(node.text.trim())
        html.tag("/kbd")
    }

    class Factory : NodeRendererFactory {
        override fun create(options: DataHolder): NodeRenderer {
            return KeystrokeNodeRenderer(options)
        }
    }
}