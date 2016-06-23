package org.elasticsearch.index.analysis;

import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.settings.IndexSettingsService;

import java.io.File;

/**
 * Created by samuel on 16/6/20.
 */
public class SmTokenizerFactory extends AbstractTokenizerFactory {
    private String segMode;

    @Inject
    public SmTokenizerFactory(Index index, IndexSettingsService indexSettingsService, Environment env,
            @Assisted String name, @Assisted Settings settings) {
        super(index, indexSettingsService.getSettings(), name, settings);

        this.segMode = settings.get("seg_mode", "index");
    }


    @Override
    public Tokenizer create() {
        return new SentenceTokenizer(this.segMode.equals("index"));
    }
}

