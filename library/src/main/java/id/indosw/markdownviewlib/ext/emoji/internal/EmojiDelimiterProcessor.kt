package id.indosw.markdownviewlib.ext.emoji.internal

import com.vladsch.flexmark.ast.Node
import com.vladsch.flexmark.internal.Delimiter
import com.vladsch.flexmark.parser.InlineParser
import com.vladsch.flexmark.parser.delimiter.DelimiterProcessor
import com.vladsch.flexmark.parser.delimiter.DelimiterRun
import com.vladsch.flexmark.util.sequence.BasedSequence
import id.indosw.markdownviewlib.ext.emoji.Emoji

class EmojiDelimiterProcessor : DelimiterProcessor {
    override fun getOpeningCharacter(): Char {
        return ':'
    }

    override fun getClosingCharacter(): Char {
        return ':'
    }

    override fun getMinLength(): Int {
        return 1
    }

    override fun getDelimiterUse(opener: DelimiterRun, closer: DelimiterRun): Int {
        return if (opener.length() >= 1 && closer.length() >= 1) {
            1
        } else {
            1
        }
    }

    override fun unmatchedDelimiterNode(inlineParser: InlineParser, delimiter: DelimiterRun): Node? {
        return null
    }

    override fun canBeOpener(
        before: String,
        after: String,
        leftFlanking: Boolean,
        rightFlanking: Boolean,
        beforeIsPunctuation: Boolean,
        afterIsPunctuation: Boolean,
        beforeIsWhitespace: Boolean,
        afterIsWhiteSpace: Boolean
    ): Boolean {
        return leftFlanking && !"0123456789".contains(before)
    }

    override fun canBeCloser(
        before: String,
        after: String,
        leftFlanking: Boolean,
        rightFlanking: Boolean,
        beforeIsPunctuation: Boolean,
        afterIsPunctuation: Boolean,
        beforeIsWhitespace: Boolean,
        afterIsWhiteSpace: Boolean
    ): Boolean {
        return rightFlanking && !"0123456789".contains(after)
    }

    override fun skipNonOpenerCloser(): Boolean {
        return true
    }

    override fun process(opener: Delimiter, closer: Delimiter, delimitersUsed: Int) {
        // Normal case, wrap nodes between delimiters in emoji node.
        // don't allow any spaces between delimiters
        if (opener.input.subSequence(opener.endIndex, closer.startIndex)
                .indexOfAny(BasedSequence.WHITESPACE_CHARS) == -1
        ) {
            val emoji = Emoji(
                opener.getTailChars(delimitersUsed),
                BasedSequence.NULL,
                closer.getLeadChars(delimitersUsed)
            )
            opener.moveNodesBetweenDelimitersTo(emoji, closer)
        } else {
            opener.convertDelimitersToText(delimitersUsed, closer)
        }
    }
}