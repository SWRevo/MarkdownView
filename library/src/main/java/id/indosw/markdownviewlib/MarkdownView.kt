@file:Suppress("DEPRECATION")

package id.indosw.markdownviewlib

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.text.TextUtils
import android.util.AttributeSet
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.orhanobut.logger.Logger
import com.vladsch.flexmark.ast.*
import com.vladsch.flexmark.ast.util.TextCollectingVisitor
import com.vladsch.flexmark.ext.abbreviation.Abbreviation
import com.vladsch.flexmark.ext.abbreviation.AbbreviationExtension
import com.vladsch.flexmark.ext.attributes.AttributesExtension
import com.vladsch.flexmark.ext.autolink.AutolinkExtension
import com.vladsch.flexmark.ext.footnotes.FootnoteExtension
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughSubscriptExtension
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.html.AttributeProvider
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.html.HtmlWriter
import com.vladsch.flexmark.html.IndependentAttributeProviderFactory
import com.vladsch.flexmark.html.renderer.*
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.superscript.SuperscriptExtension
import com.vladsch.flexmark.util.html.Attributes
import com.vladsch.flexmark.util.html.Escaping
import com.vladsch.flexmark.util.options.DataHolder
import com.vladsch.flexmark.util.options.MutableDataHolder
import com.vladsch.flexmark.util.options.MutableDataSet
import id.indosw.markdownviewlib.css.ExternalStyleSheet
import id.indosw.markdownviewlib.css.InternalStyleSheet
import id.indosw.markdownviewlib.css.StyleSheet
import id.indosw.markdownviewlib.ext.bean.BeanExtension
import id.indosw.markdownviewlib.ext.emoji.EmojiExtension
import id.indosw.markdownviewlib.ext.kbd.Keystroke
import id.indosw.markdownviewlib.ext.kbd.KeystrokeExtension
import id.indosw.markdownviewlib.ext.label.LabelExtension
import id.indosw.markdownviewlib.ext.mark.Mark
import id.indosw.markdownviewlib.ext.mark.MarkExtension
import id.indosw.markdownviewlib.ext.mathjax.MathJax
import id.indosw.markdownviewlib.ext.mathjax.MathJaxExtension
import id.indosw.markdownviewlib.ext.twitter.TwitterExtension
import id.indosw.markdownviewlib.ext.video.VideoLinkExtension
import id.indosw.markdownviewlib.js.ExternalScript
import id.indosw.markdownviewlib.js.JavaScript
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.*

@Suppress("PrivatePropertyName", "SpellCheckingInspection",
    "unused"
)
class MarkdownView @SuppressLint("SetJavaScriptEnabled") constructor(
    context: Context?,
    attrs: AttributeSet?,
    defStyleAttr: Int
) : WebView(
    context!!, attrs, defStyleAttr
) {
    private val OPTIONS: DataHolder = MutableDataSet()
        .set(FootnoteExtension.FOOTNOTE_REF_PREFIX, "[")
        .set(FootnoteExtension.FOOTNOTE_REF_SUFFIX, "]")
        .set(HtmlRenderer.FENCED_CODE_LANGUAGE_CLASS_PREFIX, "")
        .set(
            HtmlRenderer.FENCED_CODE_NO_LANGUAGE_CLASS,
            "nohighlight"
        ) //.set(FootnoteExtension.FOOTNOTE_BACK_REF_STRING, "&#8593")
    private val mStyleSheets: MutableList<StyleSheet> = LinkedList()
    private val mScripts: HashSet<JavaScript> = LinkedHashSet()
    private var mEscapeHtml = true
    var bean: Any? = null

    @JvmOverloads
    constructor(context: Context?, attrs: AttributeSet? = null) : this(context, attrs, 0)

    fun setEscapeHtml(flag: Boolean): MarkdownView {
        mEscapeHtml = flag
        return this
    }

    fun setEmojiRootPath(path: String): MarkdownView {
        (OPTIONS as MutableDataHolder).set(EmojiExtension.ROOT_IMAGE_PATH, path)
        return this
    }

    fun setEmojiImageExtension(ext: String): MarkdownView {
        (OPTIONS as MutableDataHolder).set(EmojiExtension.IMAGE_EXT, ext)
        return this
    }

    fun addStyleSheet(s: StyleSheet?): MarkdownView {
        if (s != null && !mStyleSheets.contains(s)) {
            mStyleSheets.add(s)
        }
        return this
    }

    fun replaceStyleSheet(oldStyle: StyleSheet, newStyle: StyleSheet?): MarkdownView {
        @Suppress("ControlFlowWithEmptyBody")
        if (oldStyle === newStyle) {
        } else if (newStyle == null) {
            mStyleSheets.remove(oldStyle)
        } else {
            val index = mStyleSheets.indexOf(oldStyle)
            if (index >= 0) {
                mStyleSheets[index] = newStyle
            } else {
                addStyleSheet(newStyle)
            }
        }
        return this
    }

    fun removeStyleSheet(s: StyleSheet): MarkdownView {
        mStyleSheets.remove(s)
        return this
    }

    fun addJavascript(js: JavaScript): MarkdownView {
        mScripts.add(js)
        return this
    }

    fun removeJavaScript(js: JavaScript): MarkdownView {
        mScripts.remove(js)
        return this
    }

    private fun parseBuildAndRender(text: String): String {
        val parser = Parser.builder(OPTIONS)
            .extensions(EXTENSIONS)
            .build()
        val renderer = HtmlRenderer.builder(OPTIONS)
            .escapeHtml(mEscapeHtml)
            .attributeProviderFactory(object : IndependentAttributeProviderFactory() {
                override fun create(context: NodeRendererContext): AttributeProvider {
                    return CustomAttributeProvider()
                }
            })
            .nodeRendererFactory(NodeRendererFactoryImpl())
            .extensions(EXTENSIONS)
            .build()
        return renderer.render(parser.parse(text))
    }

    fun loadMarkdown(text: String) {
        var html = parseBuildAndRender(text)
        val sb = StringBuilder()
        sb.append("<html>\n")
        sb.append("<head>\n")
        //Folha de estilo padrão.
        if (mStyleSheets.size <= 0) {
            mStyleSheets.add(InternalStyleSheet())
        }
        //Adiciona as folhas de estilo.
        for (s in mStyleSheets) {
            sb.append(s.toHTML())
        }
        //Adiciona os scripts.
        for (js in mScripts) {
            sb.append(js.toHTML())
        }
        sb.append("</head>\n")
        sb.append("<body>\n")
        sb.append("<div class='container'>\n")
        sb.append(html)
        sb.append("</div>\n")
        sb.append(
            """
    <a href='#' class='scrollup'><svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' width='25px' height='25px' viewBox='0 0 24 24' version='1.1'>
    <g><path fill='#fff' d='M 12 5.09375 L 11.28125 5.78125 L 2.28125 14.78125 L 3.71875 16.21875 L 12 7.9375 L 20.28125 16.21875 L 21.71875 14.78125 L 12.71875 5.78125 Z'></path>
    </g>
    </svg></a>
    """.trimIndent()
        )
        sb.append("</body>\n")
        sb.append("</html>")
        html = sb.toString()
        Logger.d(html)
        loadDataWithBaseURL(
            "",
            html,
            "text/html",
            "UTF-8",
            ""
        )
    }

    fun loadMarkdownFromAsset(path: String?) {
        loadMarkdown(Utils.getStringFromAssetFile(context.assets, path))
    }

    fun loadMarkdownFromFile(file: File?) {
        loadMarkdown(Utils.getStringFromFile(file))
    }

    fun loadMarkdownFromUrl(url: String?) {
        LoadMarkdownUrlTask().execute(url)
    }

    class NodeRendererFactoryImpl : NodeRendererFactory {
        override fun create(options: DataHolder): NodeRenderer {
            return NodeRenderer {
                val set = HashSet<NodeRenderingHandler<*>>()
                set.add(NodeRenderingHandler(Image::class.java) { node: Image, context: NodeRendererContext, html: HtmlWriter ->
                    if (!context.isDoNotRenderLinks) {
                        val altText = TextCollectingVisitor().collectAndGetText(node)
                        val resolvedLink =
                            context.resolveLink(LinkType.IMAGE, node.url.unescape(), null)
                        var url = resolvedLink.url
                        if (!node.urlContent.isEmpty) {
                            // reverse URL encoding of =, &
                            val content =
                                Escaping.percentEncodeUrl(node.urlContent).replace("+", "%2B")
                                    .replace("%3D", "=").replace("%26", "&amp;")
                            url += content
                        }
                        val index = url.indexOf('@')
                        if (index >= 0) {
                            val dimensions =
                                url.substring(index + 1, url.length).split("\\|").toTypedArray()
                            url = url.substring(0, index)
                            if (dimensions.size == 2) {
                                val width =
                                    if (TextUtils.isEmpty(dimensions[0])) "auto" else dimensions[0]
                                val height =
                                    if (TextUtils.isEmpty(dimensions[1])) "auto" else dimensions[1]
                                html.attr("style", "width: $width; height: $height")
                            }
                        }
                        html.attr("src", url)
                        html.attr("alt", altText)
                        if (node.title.isNotNull) {
                            html.attr("title", node.title.unescape())
                        }
                        html.srcPos(node.chars).withAttr(resolvedLink).tagVoid("img")
                    }
                })
                set
            }
        }
    }

    inner class CustomAttributeProvider : AttributeProvider {
        override fun setAttributes(node: Node, part: AttributablePart, attributes: Attributes) {
            @Suppress("ControlFlowWithEmptyBody")
            if (node is FencedCodeBlock) {
                if (part.name == "NODE") {
                    val language = node.info.toString()
                    if (!TextUtils.isEmpty(language) &&
                        language != "nohighlight"
                    ) {
                        addJavascript(HIGHLIGHTJS)
                        addJavascript(HIGHLIGHT_INIT)
                        attributes.addValue("language", language)
                        //attributes.addValue("onclick", String.format("javascript:android.onCodeTap('%s', this.textContent);",
                        //        language));
                    }
                }
            } else if (node is MathJax) {
                addJavascript(MATHJAX)
                addJavascript(MATHJAX_CONFIG)
            } else if (node is Abbreviation) {
                addJavascript(TOOLTIPSTER_JS)
                addStyleSheet(TOOLTIPSTER_CSS)
                addJavascript(TOOLTIPSTER_INIT)
                attributes.addValue("class", "tooltip")
            } else if (node is Heading) {
                //attributes.addValue("onclick", String.format("javascript:android.onHeadingTap(%d, '%s');",
                //        ((Heading) node).getLevel(), ((Heading) node).getText()));
            } else if (node is Image) {
                //attributes.addValue("onclick", String.format("javascript: android.onImageTap(this.src, this.clientWidth, this.clientHeight);"));
            } else if (node is Mark) {
                //attributes.addValue("onclick", String.format("javascript: android.onMarkTap(this.textContent)"));
            } else if (node is Keystroke) {
                //attributes.addValue("onclick", String.format("javascript: android.onKeystrokeTap(this.textContent)"));
            } else if (node is Link ||
                node is AutoLink
            ) {
                //attributes.addValue("onclick", String.format("javascript: android.onLinkTap(this.href, this.textContent)"));
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class LoadMarkdownUrlTask : AsyncTask<String?, Void?, String>() {
        override fun doInBackground(vararg params: String?): String {
            val url = params[0]
            var `is`: InputStream? = null
            return try {
                val connection = URL(url).openConnection()
                connection.readTimeout = 5000
                connection.connectTimeout = 5000
                connection.setRequestProperty("Accept-Charset", "UTF-8")
                Utils.getStringFromInputStream(
                    connection.getInputStream().also { `is` = it })
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            } finally {
                if (`is` != null) {
                    try {
                        `is`!!.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        override fun onPostExecute(s: String) {
            loadMarkdown(s)
        }
    }

    companion object {
        val JQUERY_3: JavaScript =
            ExternalScript("file:///android_asset/js/jquery-3.1.1.min.js",
                isAync = false,
                isDefer = false
            )
        val HIGHLIGHTJS: JavaScript =
            ExternalScript("file:///android_asset/js/highlight.js",
                isAync = false,
                isDefer = true
            )
        val MATHJAX: JavaScript = ExternalScript(
            "https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.2/MathJax.js?config=TeX-MML-AM_CHTML",
            isAync = true,
            isDefer = false
        )
        val MATHJAX_CONFIG: JavaScript = ExternalScript(
            "file:///android_asset/js/mathjax-config.js",
            isAync = false,
            isDefer = false,
            type = "text/x-mathjax-config"
        )
        val HIGHLIGHT_INIT: JavaScript =
            ExternalScript("file:///android_asset/js/highlight-init.js",
                isAync = false,
                isDefer = true
            )
        val TOOLTIPSTER_JS: JavaScript =
            ExternalScript("file:///android_asset/js/tooltipster.bundle.min.js",
                isAync = false,
                isDefer = true
            )
        val TOOLTIPSTER_INIT: JavaScript =
            ExternalScript("file:///android_asset/js/tooltipster-init.js",
                isAync = false,
                isDefer = true
            )
        val MY_SCRIPT: JavaScript =
            ExternalScript("file:///android_asset/js/my-script.js",
                isAync = false,
                isDefer = true
            )
        val TOOLTIPSTER_CSS: StyleSheet =
            ExternalStyleSheet("file:///android_asset/css/tooltipster.bundle.min.css")
        private val EXTENSIONS = listOf(
            TablesExtension.create(),
            TaskListExtension.create(),
            AbbreviationExtension.create(),
            AutolinkExtension.create(),
            MarkExtension.create(),
            StrikethroughSubscriptExtension.create(),
            SuperscriptExtension.create(),
            KeystrokeExtension.create(),
            MathJaxExtension.create(),
            FootnoteExtension.create(),
            EmojiExtension.create(),
            VideoLinkExtension.create(),
            TwitterExtension.create(),
            LabelExtension.create(),
            BeanExtension.create(),
            AttributesExtension.create()
        )
    }

    init {
        (OPTIONS as MutableDataHolder).set(BeanExtension.BEAN_VIEW, this)
        try {
            webChromeClient = WebChromeClient()
            settings.javaScriptEnabled = true
            settings.loadsImagesAutomatically = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            val attr = getContext().obtainStyledAttributes(attrs, R.styleable.MarkdownView)
            mEscapeHtml = attr.getBoolean(R.styleable.MarkdownView_escapeHtml, true)
            attr.recycle()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        addJavascript(JQUERY_3)
        addJavascript(MY_SCRIPT)
    }
}