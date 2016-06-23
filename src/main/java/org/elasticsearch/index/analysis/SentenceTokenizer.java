package org.elasticsearch.index.analysis;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.analysis.util.SegmentingTokenizerBase;
import org.apache.lucene.util.AttributeFactory;

/**
 * Tokenizes input text into sentences.
 * <p>
 * The output tokens can then be broken into words with {@link org.apache.lucene.analysis.miscellaneous.WordDelimiterFilter}
 * </p>
 * 
 * @lucene.experimental
 */
public final class SentenceTokenizer extends SegmentingTokenizerBase {
    private static final BreakIterator sentenceProto;
    private final CharTermAttribute termAtt;
    private final OffsetAttribute offsetAtt;
    private final TypeAttribute typeAtt;
    private Iterator<SegToken> tokens;
    private JiebaSegmenter segmenter;

    private Iterator<SegToken> tokenIter;
    private List<SegToken> array;

    public SentenceTokenizer() {
        this(DEFAULT_TOKEN_ATTRIBUTE_FACTORY);
    }

    /**
     * End of sentence punctuation: 。，！？；,!?;
     */
    private final static String PUNCTION = "。，！？；,!?;";
    private final static String SPACES = " 　\t\r\n";

    private int tokenStart = 0, tokenEnd = 0;

    public SentenceTokenizer(AttributeFactory factory) {
        super(factory, (BreakIterator)sentenceProto.clone());
        this.termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
        this.offsetAtt = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);
        this.typeAtt = (TypeAttribute)this.addAttribute(TypeAttribute.class);
        segmenter = new JiebaSegmenter();
    }

    protected void setNextSentence(int sentenceStart, int sentenceEnd) {
        String sentence = new String(this.buffer, sentenceStart, sentenceEnd - sentenceStart);
        this.tokens = this.segmenter.process(sentence, JiebaSegmenter.SegMode.INDEX).iterator();
    }

    @Override
    public boolean incrementWord() {
        if(this.tokens != null && this.tokens.hasNext()) {
            SegToken token = (SegToken)this.tokens.next();
            this.clearAttributes();
            this.termAtt.append(token.toString());
            this.offsetAtt.setOffset(this.correctOffset(token.startOffset), this.correctOffset(token.endOffset));
            this.typeAtt.setType("word");
            return true;
        } else {
            return false;
        }
    }

    /*
    @Override
    public boolean incrementToken() throws IOException {
        clearAttributes();
        buffer.setLength(0);
        int ci;
        char ch, pch;
        boolean atBegin = true;
        tokenStart = tokenEnd;
        ci = input.read();
        ch = (char) ci;

        while (true) {
            if (ci == -1) {
                break;
            } else if (PUNCTION.indexOf(ch) != -1) {
                // End of a sentence
                buffer.append(ch);
                tokenEnd++;
                break;
            } else if (atBegin && SPACES.indexOf(ch) != -1) {
                tokenStart++;
                tokenEnd++;
                ci = input.read();
                ch = (char) ci;
            } else {
                buffer.append(ch);
                atBegin = false;
                tokenEnd++;
                pch = ch;
                ci = input.read();
                ch = (char) ci;
                // Two spaces, such as CR, LF
                if (SPACES.indexOf(ch) != -1 && SPACES.indexOf(pch) != -1) {
                    // buffer.append(ch);
                    tokenEnd++;
                    break;
                }
            }
        }
        if (buffer.length() == 0)
            return false;
        else {
            termAtt.setEmpty().append(buffer);
            offsetAtt.setOffset(correctOffset(tokenStart),
                    correctOffset(tokenEnd));
            typeAtt.setType("sentence");
            return true;
        }
    }
    */

    @Override
    public void reset() throws IOException {
        super.reset();
        tokenStart = tokenEnd = 0;
        this.tokens = null;
    }

    static {
        sentenceProto = BreakIterator.getSentenceInstance(Locale.ROOT);
    }

}
