package org.elasticsearch.index.analysis;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.WordlistLoader;
import org.apache.lucene.util.IOUtils;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;

import com.huaban.analysis.jieba.WordDictionary;

public final class SmAnalyzer extends Analyzer {
	private final ESLogger log = Loggers.getLogger(SmAnalyzer.class);

	private final CharArraySet stopWords;

	private static final String DEFAULT_STOPWORD_FILE = "stopwords.txt";

	private static final String STOPWORD_FILE_COMMENT = "//";

	/**
	 * Returns an unmodifiable instance of the default stop-words set.
	 *
	 * @return an unmodifiable instance of the default stop-words set.
	 */
	public static CharArraySet getDefaultStopSet() {
		return DefaultSetHolder.DEFAULT_STOP_SET;
	}

	/**
	 * Atomically loads the DEFAULT_STOP_SET in a lazy fashion once the outer
	 * class accesses the static final set the first time.;
	 */
	private static class DefaultSetHolder {
		static final CharArraySet DEFAULT_STOP_SET;

		static {
			try {
				DEFAULT_STOP_SET = loadDefaultStopWordSet();
			} catch (IOException ex) {
				// default set should always be present as it is part of the
				// distribution (JAR)
				throw new RuntimeException(
						"Unable to load default stopword set");
			}
		}

		static CharArraySet loadDefaultStopWordSet() throws IOException {
			// make sure it is unmodifiable as we expose it in the outer class
			return CharArraySet.unmodifiableSet(WordlistLoader.getWordSet(
					IOUtils.getDecodingReader(SmAnalyzer.class,
							DEFAULT_STOPWORD_FILE, StandardCharsets.UTF_8),
					STOPWORD_FILE_COMMENT));
		}
	}

	private File configFile;
	private String type;

	private CharArraySet loadStopWords(File configFile) {
		try {
			return CharArraySet.unmodifiableSet(WordlistLoader.getWordSet(
					new FileReader(new File(new File(configFile, "jieba"), "stopwords.txt")), STOPWORD_FILE_COMMENT));
		} catch (IOException e) {
			return DefaultSetHolder.DEFAULT_STOP_SET;
		}
	}

	public SmAnalyzer(Environment env, Settings settings) {
		super();
		type = settings.get("seg_mode", "index");
		boolean stop = settings.getAsBoolean("stop", true);

		configFile = env.configFile().toFile();
		this.stopWords = stop ? this.loadStopWords(configFile) : CharArraySet.EMPTY_SET;
		WordDictionary.getInstance().init( new File(configFile, "jieba").toPath());

		this.log.info("SmAnalyzer stopWords = {}", this.stopWords.toString());
	}

	public SmAnalyzer(String segMode, File configFile, boolean isStop) {
		super();

		this.type = segMode;
		this.configFile = configFile;
		WordDictionary.getInstance().init( new File(configFile, "jieba").toPath());
		this.stopWords = isStop ? this.loadStopWords(configFile)
				: CharArraySet.EMPTY_SET;

		this.log.info("SmAnalyzer isStop = {}", isStop);
		this.log.info("SmAnalyzer stopWords = {}", this.stopWords.toString());
	}

	/**
	 * 重载Analyzer接口，构造分词组件
	 */
	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		Tokenizer tokenizer = new SentenceTokenizer();
		Object result = tokenizer;

		result = new PorterStemFilter((TokenStream)result);
		if(!this.stopWords.isEmpty()) {
			result = new StopFilter((TokenStream)result, this.stopWords);
		}

		return new TokenStreamComponents((Tokenizer)tokenizer, (TokenStream)result);
	}
}
