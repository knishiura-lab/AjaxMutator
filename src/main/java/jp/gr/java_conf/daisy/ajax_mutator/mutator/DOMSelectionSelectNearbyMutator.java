package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import jp.gr.java_conf.daisy.ajax_mutator.JSType;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMSelection;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.Mutation;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.AbstractMutator;
import jp.gr.java_conf.daisy.ajax_mutator.util.Randomizer;
import jp.gr.java_conf.daisy.ajax_mutator.util.StringToAst;
import org.mozilla.javascript.ast.AstNode;

/**
 * {@link jp.gr.java_conf.daisy.ajax_mutator.mutator.Mutator} that creates mutation for {@link DOMSelection} of selecting
 * nearby element of originally selecting element.
 *
 * @author Kazuki Nishiura
 */
public class DOMSelectionSelectNearbyMutator extends AbstractMutator<DOMSelection> {
    public DOMSelectionSelectNearbyMutator() {
        super(DOMSelection.class);
    }

    @Override
    public Mutation generateMutation(DOMSelection originalNode) {
        double randomValue = Randomizer.getDouble();
        AstNode node = originalNode.getAstNode();
        JSType domType
                = (originalNode.getSelectionMethod() == DOMSelection.SelectionMethod.JQUERY)
                ? JSType.JQUERY_OBJECT
                : JSType.DOM_ELEMENT;

        if (randomValue < 0.5) {
            return new Mutation(
                    originalNode.getAstNode(),
                    StringToAst.createParentNodeAsString(node, domType));
        } else {
            return new Mutation(
                    originalNode.getAstNode(),
                    StringToAst.createChildNodeAsString(node, domType));
        }
    }
}
