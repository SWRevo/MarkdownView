@file:Suppress("unused", "SpellCheckingInspection")

package id.indosw.markdownviewlib.css

import android.text.TextUtils
import com.orhanobut.logger.Logger
import java.util.*

open class InternalStyleSheet : StyleSheet {
    private val mRules: MutableMap<String, MutableMap<String, MutableMap<String, String>>> =
        LinkedHashMap()
    private val mFontFaces: MutableMap<String, String> = LinkedHashMap()
    private var currentMediaQuery: String
    override fun toString(): String {
        val sb = StringBuilder()
        for ((_, value) in mFontFaces) {
            sb.append("@font-face {")
            sb.append(value)
            sb.append("}\n")
        }
        for ((key, value) in mRules) {
            if (key != NO_MEDIA_QUERY) {
                sb.append("@media ")
                sb.append(key)
                sb.append(" {\n")
            }
            for ((key1, value1) in value) {
                sb.append(key1)
                sb.append(" {")
                for ((key2, value2) in value1) {
                    sb.append(key2)
                    sb.append(":")
                    sb.append(value2)
                    sb.append(";")
                }
                sb.append("}\n")
            }
            if (key != NO_MEDIA_QUERY) {
                sb.append("}\n")
            }
        }
        return sb.toString()
    }

    override fun toHTML(): String {
        return """
            <style>
            ${toString()}
            </style>
            
            """.trimIndent()
    }

    private fun getCurrentMediaQuery(): MutableMap<String, MutableMap<String, String>> {
        return mRules[currentMediaQuery]!!
    }

    fun addMedia(varMediaQuery: String?) {
        var mediaQuery = varMediaQuery
        if (mediaQuery != null && mediaQuery.trim { it <= ' ' }.isNotEmpty()) {
            mediaQuery = mediaQuery.trim { it <= ' ' }
            if (!mRules.containsKey(mediaQuery)) {
                mRules[mediaQuery] =
                    LinkedHashMap()
                currentMediaQuery = mediaQuery
            }
        }
    }

    fun addFontFace(
        fontFamily: String, fontStretch: String?, fontStyle: String?, fontWeight: String?,
        vararg src: String?
    ) {
        if (!TextUtils.isEmpty(fontFamily) && src.isNotEmpty()) {
            val sb = StringBuilder()
            sb.append("font-family:").append(fontFamily).append(";")
            sb.append("font-stretch:")
                .append(if (TextUtils.isEmpty(fontStretch)) "normal" else fontStretch).append(";")
            sb.append("font-style:")
                .append(if (TextUtils.isEmpty(fontStyle)) "normal" else fontStyle).append(";")
            sb.append("font-weight:")
                .append(if (TextUtils.isEmpty(fontWeight)) "normal" else fontWeight).append(";")
            sb.append("src:")
            for (i in src.indices) {
                sb.append(src[i])
                if (i < src.size - 1) sb.append(",")
            }
            sb.append(";")
            mFontFaces[fontFamily.trim { it <= ' ' }] = sb.toString()
        }
    }

    fun endMedia() {
        currentMediaQuery = NO_MEDIA_QUERY
    }

    fun addRule(varSelector: String?, vararg declarations: String?) {
        var selector = varSelector
        if (selector != null && selector.trim { it <= ' ' }.isNotEmpty() && declarations.isNotEmpty()) {
            selector = selector.trim { it <= ' ' }
            if (!getCurrentMediaQuery().containsKey(selector)) {
                getCurrentMediaQuery()[selector] = LinkedHashMap()
            }
            for (declaration in declarations) {
                //String vazia.
                if (declaration == null || declaration.trim { it <= ' ' }.isEmpty()) continue
                val nameAndValue = declaration.trim { it <= ' ' }.split(":").toTypedArray()
                if (nameAndValue.size == 2) {
                    val name = nameAndValue[0].trim { it <= ' ' }
                    val value = nameAndValue[1].trim { it <= ' ' }
                    getCurrentMediaQuery()[selector]!![name] = value
                } else {
                    Logger.e("invalid css: '$declaration' in selector: $selector")
                }
            }
        }
    }

    fun removeRule(selector: String) {
        getCurrentMediaQuery().remove(selector)
    }

    fun removeDeclaration(selector: String, declarationName: String) {
        if (!TextUtils.isEmpty(selector) && getCurrentMediaQuery().containsKey(selector)) {
            getCurrentMediaQuery()[selector]!!.remove(declarationName)
        }
    }

    fun replaceDeclaration(selector: String, declarationName: String, newDeclarationValue: String) {
        if (!TextUtils.isEmpty(selector) && !TextUtils.isEmpty(declarationName)) {
            if (getCurrentMediaQuery().containsKey(selector) && getCurrentMediaQuery()[selector]!!
                    .containsKey(declarationName)
            ) {
                getCurrentMediaQuery()[selector]!![declarationName] = newDeclarationValue
            }
        }
    }

    companion object {
        private const val NO_MEDIA_QUERY = "NO_MEDIA_QUERY"
    }

    init {
        currentMediaQuery = NO_MEDIA_QUERY
        mRules[currentMediaQuery] =
            LinkedHashMap()
        //Estilos padrões.
        //Alinhamento de Texto
        addRule("p", "text-align: left")
        addRule(".text-left", "text-align: left")
        addRule(".text-right", "text-align: right")
        addRule(".text-center", "text-align: center")
        addRule(".text-justify", "text-align: justify")
        //Cores.
        addRule("red, .red", "color: #f44336")
        addRule("pink, .pink", "color: #E91E63")
        addRule("purple, .purple", "color: #9C27B0")
        addRule("deeppurple, .deeppurple", "color: #673AB7")
        addRule("indigo, .indigo", "color: #3F51B5")
        addRule("blue, .blue", "color: #2196F3")
        addRule("lightblue, .lightblue", "color: #03A9F4")
        addRule("cyan, .cyan", "color: #00BCD4")
        addRule("teal, .teal", "color: #009688")
        addRule("green, .green", "color: #4CAF50")
        addRule("lightgreen, .lightgreen", "color: #8BC34A")
        addRule("lime, .lime", "color: #CDDC39")
        addRule("yellow, .yellow", "color: #FFEB3B")
        addRule("amber, .amber", "color: #FFC107")
        addRule("orange, .orange", "color: #FF9800")
        addRule("deeporange, .deeporange", "color: #FF5722")
        addRule("brown, .brown", "color: #795548")
        addRule("grey, .grey", "color: #9E9E9E")
        addRule("bluegrey, .bluegrey", "color: #607D8B")
        //Tamanho de texto.
        addRule("smaller, .text-smaller", "font-size: smaller")
        addRule("small, .text-small", "font-size: small")
        addRule("medium, .text-medium", "font-size: medium")
        addRule("large, .text-large", "font-size: large")
        addRule("larger, .text-larger", "font-size: larger")
        addRule("x-small, .text-x-small", "font-size: x-small")
        addRule("x-large, .text-x-large", "font-size: x-large")
        addRule("xx-small, .text-xx-small", "font-size: xx-small")
        addRule("xx-large, .text-xx-large", "font-size: xx-large")
        //Botão voltar ao topo.
        addRule("body", "margin-bottom: 50px !important")
        addRule(
            ".scrollup",
            "width: 55px",
            "height: 55px",
            "position: fixed",
            "bottom: 15px",
            "right: 15px",
            "visibility: hidden",
            "display: flex",
            "align-items: center",
            "justify-content: center",
            "margin: 0 !important",
            "line-height: 70px",
            "box-shadow: 0 0 4px rgba(0, 0, 0, 0.14), 0 4px 8px rgba(0, 0, 0, 0.28)",
            "border-radius: 50%",
            "color: #fff",
            "padding: 5px"
        )
    }
}