package id.indosw.markdownviewlib.ext.video

import com.vladsch.flexmark.Extension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.html.HtmlRenderer.HtmlRendererExtension
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.parser.Parser.ParserExtension
import com.vladsch.flexmark.util.options.MutableDataHolder
import id.indosw.markdownviewlib.ext.video.internal.VideoLinkNodePostProcessor
import id.indosw.markdownviewlib.ext.video.internal.VideoLinkNodeRenderer

class VideoLinkExtension private constructor() : ParserExtension, HtmlRendererExtension {
    override fun extend(parserBuilder: Parser.Builder) {
        parserBuilder.postProcessorFactory(VideoLinkNodePostProcessor.Factory(parserBuilder))
    }

    override fun rendererOptions(options: MutableDataHolder) {}
    override fun parserOptions(options: MutableDataHolder) {}
    override fun extend(rendererBuilder: HtmlRenderer.Builder, rendererType: String) {
        if (rendererType == "HTML") {
            rendererBuilder.nodeRendererFactory(VideoLinkNodeRenderer.Factory())
        }
    }

    companion object {
        fun create(): Extension {
            return VideoLinkExtension()
        }
    }
}