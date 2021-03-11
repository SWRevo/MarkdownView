package id.indosw.markdownviewlib.ext.kbd

import com.vladsch.flexmark.Extension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.html.HtmlRenderer.HtmlRendererExtension
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.parser.Parser.ParserExtension
import com.vladsch.flexmark.util.options.MutableDataHolder
import id.indosw.markdownviewlib.ext.kbd.internal.KeystrokeDelimiterProcessor
import id.indosw.markdownviewlib.ext.kbd.internal.KeystrokeNodeRenderer

class KeystrokeExtension private constructor() : ParserExtension, HtmlRendererExtension {
    override fun rendererOptions(options: MutableDataHolder) {}
    override fun parserOptions(options: MutableDataHolder) {}
    override fun extend(parserBuilder: Parser.Builder) {
        parserBuilder.customDelimiterProcessor(KeystrokeDelimiterProcessor())
    }

    override fun extend(rendererBuilder: HtmlRenderer.Builder, rendererType: String) {
        if ("HTML" == rendererType) {
            rendererBuilder.nodeRendererFactory(KeystrokeNodeRenderer.Factory())
        }
    }

    companion object {
        fun create(): Extension {
            return KeystrokeExtension()
        }
    }
}