package org.elasticsearch.index.analysis;

import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.settings.IndexSettingsService;

/**
 * Created by samuel on 16/6/20.
 */
public class SmTokenizerFactory extends AbstractTokenizerFactory {
    private final Settings settings;

    @Inject
    public SmTokenizerFactory(Index index, IndexSettingsService indexSettingsService, Environment env,
            @Assisted String name, @Assisted Settings settings) {
        super(index, indexSettingsService.getSettings(), name, settings);
        this.settings=settings;
    }


    @Override
    public Tokenizer create() {
        return new SentenceTokenizer();  }
}
