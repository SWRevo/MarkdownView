package id.indosw.markdownviewlib.ext.mark

import com.vladsch.flexmark.Extension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.html.HtmlRenderer.HtmlRendererExtension
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.parser.Parser.ParserExtension
import com.vladsch.flexmark.util.options.DataHolder
import com.vladsch.flexmark.util.options.MutableDataHolder
import id.indosw.markdownviewlib.ext.mark.internal.MarkDelimiterProcessor
import id.indosw.markdownviewlib.ext.mark.internal.MarkNodeRenderer

class MarkExtension private constructor() : ParserExtension, HtmlRendererExtension {
    override fun rendererOptions(options: MutableDataHolder) {}
    override fun parserOptions(options: MutableDataHolder) {}
    override fun extend(parserBuilder: Parser.Builder) {
        parserBuilder.customDelimiterProcessor(MarkDelimiterProcessor())
    }

    override fun extend(rendererBuilder: HtmlRenderer.Builder, rendererType: String) {
        if ("HTML" == rendererType) {
            rendererBuilder.nodeRendererFactory { options: DataHolder? -> MarkNodeRenderer(options) }
        }
    }

    companion object {
        fun create(): Extension {
            return MarkExtension()
        }
    }
}