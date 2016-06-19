package org.elasticsearch.plugin.analysis.sm;

import org.elasticsearch.common.inject.Module;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.index.analysis.SmAnalysisBinderProcessor;
import org.elasticsearch.indices.analysis.SmIndicesAnalysisModule;
import org.elasticsearch.plugins.Plugin;

import java.util.Collection;
import java.util.Collections;


public class AnalysisSmPlugin extends Plugin {
	
	public static String PLUGIN_NAME = "analysis-sm";

    @Override public String name() {
        return PLUGIN_NAME;
    }


    @Override public String description() {
        return PLUGIN_NAME;
    }

    @Override
    public Collection<Module> nodeModules() {
        return Collections.<Module>singletonList(new SmIndicesAnalysisModule());
    }
    public void onModule(AnalysisModule module) {
        module.addProcessor(new SmAnalysisBinderProcessor());
    }

}
