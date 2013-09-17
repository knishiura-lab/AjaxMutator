package jp.gr.java_conf.daisy.ajax_mutator.mutation_generator;

import org.mozilla.javascript.ast.AstNode;

import java.io.InputStream;
import java.util.List;

public class UnifiedDiffGeneratorForTest extends UnifiedDiffGenerator {
    public UnifiedDiffGeneratorForTest(InputStream inputStream) {
        super("fake.js", 100000000L, inputStream);
    }

    @Override
    public String generateUnifiedDiff(
            AstNode mutatedNode, List<String> mutatingContent) {
        return super.generateUnifiedDiff(mutatedNode, mutatingContent);
    }
}
