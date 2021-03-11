package id.indosw.markdownviewlib.ext.bean

import com.vladsch.flexmark.Extension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.html.HtmlRenderer.HtmlRendererExtension
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.parser.Parser.ParserExtension
import com.vladsch.flexmark.util.options.DataKey
import com.vladsch.flexmark.util.options.MutableDataHolder
import id.indosw.markdownviewlib.MarkdownView
import id.indosw.markdownviewlib.ext.bean.internal.BeanDelimiterProcessor
import id.indosw.markdownviewlib.ext.bean.internal.BeanNodeRenderer

class BeanExtension private constructor() : ParserExtension, HtmlRendererExtension {
    override fun rendererOptions(options: MutableDataHolder) {}
    override fun parserOptions(options: MutableDataHolder) {}
    override fun extend(parserBuilder: Parser.Builder) {
        parserBuilder.customDelimiterProcessor(BeanDelimiterProcessor())
    }

    override fun extend(rendererBuilder: HtmlRenderer.Builder, rendererType: String) {
        if ("HTML" == rendererType) {
            rendererBuilder.nodeRendererFactory(BeanNodeRenderer.Factory())
        }
    }

    companion object {
        val BEAN_VIEW = DataKey("BEAN_VIEW", null as MarkdownView?)
        fun create(): Extension {
            return BeanExtension()
        }
    }
}