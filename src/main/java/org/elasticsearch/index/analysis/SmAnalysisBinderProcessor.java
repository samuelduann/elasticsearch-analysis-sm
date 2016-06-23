package org.elasticsearch.index.analysis;

public class SmAnalysisBinderProcessor extends
        AnalysisModule.AnalysisBinderProcessor {

    /*
    @Override
    public void processTokenFilters(TokenFiltersBindings tokenFiltersBindings) {
        tokenFiltersBindings.processTokenFilter("sm",
                SmTokenFilterFactory.class);
        super.processTokenFilters(tokenFiltersBindings);
    }
    */

    @Override
    public void processAnalyzers(AnalyzersBindings analyzersBindings) {
        analyzersBindings.processAnalyzer("sm", SmAnalyzerProvider.class);
    }

    @Override
    public void processTokenizers(TokenizersBindings tokenizersBindings) {
        tokenizersBindings.processTokenizer("sm", SmTokenizerFactory.class);
    }
}
