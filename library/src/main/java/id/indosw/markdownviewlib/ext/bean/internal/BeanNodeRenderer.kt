@file:Suppress("SpellCheckingInspection")

package id.indosw.markdownviewlib.ext.bean.internal

import com.orhanobut.logger.Logger
import com.vladsch.flexmark.html.HtmlWriter
import com.vladsch.flexmark.html.renderer.NodeRenderer
import com.vladsch.flexmark.html.renderer.NodeRendererContext
import com.vladsch.flexmark.html.renderer.NodeRendererFactory
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler
import com.vladsch.flexmark.util.options.DataHolder
import id.indosw.markdownviewlib.MarkdownView
import id.indosw.markdownviewlib.ext.bean.Bean
import id.indosw.markdownviewlib.ext.bean.BeanExtension
import java.lang.reflect.Method
import java.util.*

class BeanNodeRenderer(options: DataHolder) : NodeRenderer {
    private val mMarkdownView: MarkdownView? = options.get(BeanExtension.BEAN_VIEW)
    override fun getNodeRenderingHandlers(): Set<NodeRenderingHandler<*>> {
        val set = HashSet<NodeRenderingHandler<*>>()
        set.add(NodeRenderingHandler(Bean::class.java) { node: Bean, context: NodeRendererContext, html: HtmlWriter ->
            render(
                node,
                context,
                html
            )
        })
        return set
    }

    private fun render(node: Bean, context: NodeRendererContext, html: HtmlWriter) {
        if (mMarkdownView?.bean != null) {
            val name = node.text.toString().trim { it <= ' ' }
            val value = getBeanAttributeValue(name, mMarkdownView.bean)
            if (value != null) {
                html.attr("class", "bean")
                html.withAttr().tag("span")
                html.append(value.toString())
                html.tag("/span")
            }
        } else {
            context.renderChildren(node)
        }
    }

    class Factory : NodeRendererFactory {
        override fun create(options: DataHolder): NodeRenderer {
            return BeanNodeRenderer(options)
        }
    }

    companion object {
        private fun getBeanAttributeValue(name: String, bean: Any?): Any? {
            //Pega o tipo do bean.
            val beanClass: Class<*> = bean!!.javaClass
            //Logger.d("buscando %s em %s", name, beanClass.getSimpleName());
            //Pega todos o caminho do membro separadamente.
            val fieldNames = name.split("\\.").toTypedArray()
            //O caminho é válido.
            if (fieldNames.isNotEmpty()) {
                val methodName = fieldNames[0]
                val methodNameCamelCase = Character.toUpperCase(methodName[0]).toString() +
                        methodName.substring(1)
                //Tantar usar o xxx().
                val fieldMethod: Method? = try {
                    beanClass.getMethod(methodName)
                    //Logger.d("encontrado %s", methodName);
                } catch (e: NoSuchMethodException) {
                    //Tentar usar o getXxx().
                    try {
                        beanClass.getMethod("get$methodNameCamelCase")
                        //Logger.d("encontrado get%s", methodNameCamelCase);
                    } catch (e1: NoSuchMethodException) {
                        //Tentar usar o isXxx().
                        try {
                            beanClass.getMethod("is$methodNameCamelCase")
                            //Logger.d("encontrado is%", methodNameCamelCase);
                        } catch (e2: NoSuchMethodException) {
                            Logger.d("NoSuchMethodException: ", methodName)
                            return null
                        }
                    }
                }
                if (fieldMethod != null) {
                    val o: Any?
                    try {
                        fieldMethod.isAccessible = true
                        o = fieldMethod.invoke(bean)
                    } catch (e: Exception) {
                        return null
                    }
                    if (o == null) {
                        return null
                    }
                    //Obter o objeto se não houver mais membros.
                    return if (fieldNames.size == 1) {
                        o
                    } else {
                        getBeanAttributeValue(name.substring(name.indexOf(".") + 1), o)
                    }
                }
            }
            //Erro
            return null
        }
    }

}