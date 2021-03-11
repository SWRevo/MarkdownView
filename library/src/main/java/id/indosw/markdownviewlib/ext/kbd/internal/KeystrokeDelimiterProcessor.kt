package id.indosw.markdownviewlib.ext.kbd.internal

import com.vladsch.flexmark.ast.Node
import com.vladsch.flexmark.internal.Delimiter
import com.vladsch.flexmark.parser.InlineParser
import com.vladsch.flexmark.parser.delimiter.DelimiterProcessor
import com.vladsch.flexmark.parser.delimiter.DelimiterRun
import com.vladsch.flexmark.util.sequence.BasedSequence
import id.indosw.markdownviewlib.ext.kbd.Keystroke

class KeystrokeDelimiterProcessor : DelimiterProcessor {
    override fun getOpeningCharacter(): Char {
        return '@'
    }

    override fun getClosingCharacter(): Char {
        return '@'
    }

    override fun getMinLength(): Int {
        return 1
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
        return if (opener.length() >= 1 && closer.length() >= 1) {
            // Use exactly two delimiters even if we have more, and don't care about internal openers/closers.
            1
        } else {
            0
        }
    }

    override fun process(opener: Delimiter, closer: Delimiter, delimitersUsed: Int) {
        // wrap nodes between delimiters in strikethrough.
        val kbd = Keystroke(
            opener.getTailChars(delimitersUsed),
            BasedSequence.NULL,
            closer.getLeadChars(delimitersUsed)
        )
        opener.moveNodesBetweenDelimitersTo(kbd, closer)
    }
}