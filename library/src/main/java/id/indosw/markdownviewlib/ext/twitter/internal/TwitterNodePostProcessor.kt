package id.indosw.markdownviewlib.ext.twitter.internal

import com.vladsch.flexmark.ast.Document
import com.vladsch.flexmark.ast.Link
import com.vladsch.flexmark.ast.Node
import com.vladsch.flexmark.ast.Text
import com.vladsch.flexmark.parser.block.NodePostProcessor
import com.vladsch.flexmark.parser.block.NodePostProcessorFactory
import com.vladsch.flexmark.util.NodeTracker
import com.vladsch.flexmark.util.options.DataHolder
import id.indosw.markdownviewlib.ext.twitter.Twitter

class TwitterNodePostProcessor(options: DataHolder?) : NodePostProcessor() {
    override fun process(state: NodeTracker, node: Node) {
        if (node is Link) {
            val previous = node.getPrevious()
            if (previous is Text) {
                val chars = previous.getChars()

                //Se o nó anterior termina com '#' e é seguido pelo Link
                if (chars.endsWith("#") && chars.isContinuedBy(node.getChars())) {
                    //Remove o caractere '#' do nó anterior.
                    previous.setChars(chars.subSequence(0, chars.length - 1))
                    val videoLink = Twitter(node)
                    videoLink.takeChildren(node)
                    node.unlink()
                    previous.insertAfter(videoLink)
                    state.nodeRemoved(node)
                    state.nodeAddedWithChildren(videoLink)
                }
            }
        }
    }

    class Factory(options: DataHolder?) : NodePostProcessorFactory(false) {
        override fun create(document: Document): NodePostProcessor {
            return TwitterNodePostProcessor(document)
        }

        init {
            addNodes(Link::class.java)
        }
    }
}