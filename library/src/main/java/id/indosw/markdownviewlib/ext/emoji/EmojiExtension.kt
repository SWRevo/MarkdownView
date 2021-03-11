package id.indosw.markdownviewlib.ext.emoji

import com.vladsch.flexmark.Extension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.html.HtmlRenderer.HtmlRendererExtension
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.parser.Parser.ParserExtension
import com.vladsch.flexmark.util.options.DataKey
import com.vladsch.flexmark.util.options.MutableDataHolder
import id.indosw.markdownviewlib.ext.emoji.internal.EmojiDelimiterProcessor
import id.indosw.markdownviewlib.ext.emoji.internal.EmojiNodeRenderer

/**
 * Extension for emoji shortcuts using EmojiOne.
 */
class EmojiExtension private constructor() : ParserExtension, HtmlRendererExtension {
    override fun rendererOptions(options: MutableDataHolder) {}
    override fun parserOptions(options: MutableDataHolder) {}
    override fun extend(parserBuilder: Parser.Builder) {
        parserBuilder.customDelimiterProcessor(EmojiDelimiterProcessor())
    }

    override fun extend(rendererBuilder: HtmlRenderer.Builder, rendererType: String) {
        if (rendererType == "HTML") {
            rendererBuilder.nodeRendererFactory(EmojiNodeRenderer.Factory())
        }
    }

    companion object {
        @JvmField
        val ATTR_ALIGN = DataKey("ATTR_ALIGN", "absmiddle")
        @JvmField
        val ATTR_IMAGE_SIZE = DataKey("ATTR_IMAGE_SIZE", "20")
        @JvmField
        val ROOT_IMAGE_PATH = DataKey("ROOT_IMAGE_PATH", "file:///android_asset/svg/")
        @JvmField
        val IMAGE_EXT = DataKey("IMAGE_EXT", "svg")
        fun create(): Extension {
            return EmojiExtension()
        }
    }
}