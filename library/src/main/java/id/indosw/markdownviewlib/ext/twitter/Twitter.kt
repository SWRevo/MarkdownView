package id.indosw.markdownviewlib.ext.twitter

import com.vladsch.flexmark.ast.InlineLinkNode
import com.vladsch.flexmark.ast.Link
import com.vladsch.flexmark.util.sequence.BasedSequence

class Twitter(other: Link) : InlineLinkNode(
    other.chars.baseSubSequence(other.chars.startOffset - 1, other.chars.endOffset),
    other.chars.baseSubSequence(other.chars.startOffset - 1, other.textOpeningMarker.endOffset),
    other.text,
    other.textClosingMarker,
    other.linkOpeningMarker,
    other.url,
    other.titleOpeningMarker,
    other.title,
    other.titleClosingMarker,
    other.linkClosingMarker
) {
    override fun setTextChars(textChars: BasedSequence) {
        val textCharsLength = textChars.length
        textOpeningMarker = textChars.subSequence(0, 1)
        text = textChars.subSequence(1, textCharsLength - 1).trim()
        textClosingMarker = textChars.subSequence(textCharsLength - 1, textCharsLength)
    }
}