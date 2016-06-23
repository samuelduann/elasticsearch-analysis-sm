package org.elasticsearch.indices.analysis;

import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.analysis.*;

/**
 * Registers indices level analysis components so, if not explicitly configured,
 * will be shared among all indices.
 */
public class SmIndicesAnalysis extends AbstractComponent {

    @Inject
    public SmIndicesAnalysis(final Settings settings,
                             IndicesAnalysisService indicesAnalysisService,Environment env) {
        super(settings);

        indicesAnalysisService.analyzerProviderFactories().put("sm",
                new PreBuiltAnalyzerProviderFactory("sm", AnalyzerScope.GLOBAL,
                        new SmAnalyzer(env, settings)));

        indicesAnalysisService.tokenizerFactories().put("sentence",
                new PreBuiltTokenizerFactoryFactory(new TokenizerFactory() {
                    @Override
                    public String name() {
                        return "sentence";
                    }

                    @Override
                    public Tokenizer create() {
                        return new SentenceTokenizer(settings.get("seg_mode", "index").equals("index"));
                    }
                }));

        indicesAnalysisService.tokenizerFactories().put("other",
                new PreBuiltTokenizerFactoryFactory(new TokenizerFactory() {
                    @Override
                    public String name() {
                        return "other";
                    }

                    @Override
                    public Tokenizer create() {
                        return new OtherTokenizer();
                    }
                }));

    }
}