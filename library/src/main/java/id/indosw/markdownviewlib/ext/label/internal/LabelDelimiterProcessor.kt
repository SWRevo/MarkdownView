package id.indosw.markdownviewlib.ext.label.internal

import com.vladsch.flexmark.ast.Node
import com.vladsch.flexmark.internal.Delimiter
import com.vladsch.flexmark.parser.InlineParser
import com.vladsch.flexmark.parser.delimiter.DelimiterProcessor
import com.vladsch.flexmark.parser.delimiter.DelimiterRun
import com.vladsch.flexmark.util.sequence.BasedSequence
import id.indosw.markdownviewlib.ext.label.Label

class LabelDelimiterProcessor : DelimiterProcessor {
    override fun getOpeningCharacter(): Char {
        return '-'
    }

    override fun getClosingCharacter(): Char {
        return '-'
    }

    override fun getMinLength(): Int {
        return 2
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
        return leftFlanking
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
        return rightFlanking
    }

    override fun skipNonOpenerCloser(): Boolean {
        return false
    }

    override fun getDelimiterUse(opener: DelimiterRun, closer: DelimiterRun): Int {
        return if (opener.length() >= 2 && closer.length() >= 2) {
            // Use exactly two delimiters even if we have more, and don't care about internal openers/closers.
            opener.length().coerceAtMost(opener.length())
        } else {
            0
        }
    }

    override fun process(opener: Delimiter, closer: Delimiter, delimitersUsed: Int) {
        val lbl = Label(
            delimitersUsed,
            opener.getTailChars(delimitersUsed),
            BasedSequence.NULL,
            closer.getLeadChars(delimitersUsed)
        )
        opener.moveNodesBetweenDelimitersTo(lbl, closer)
    }
}