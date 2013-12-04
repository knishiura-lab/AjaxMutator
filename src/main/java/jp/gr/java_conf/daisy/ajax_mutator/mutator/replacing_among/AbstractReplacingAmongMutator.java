package jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Mutatable;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.Mutation;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.AbstractMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.Mutator;
import jp.gr.java_conf.daisy.ajax_mutator.util.Randomizer;
import org.mozilla.javascript.ast.AstNode;

import java.util.*;

/**
 * Abstract class for easy implementation of {@link Mutator} by replacing
 * one ASTNode with another ASTNode in the same program and same semantical class
 * (e.g., event type).
 *
 * @author Kazuki Nishiura
 */
public abstract class AbstractReplacingAmongMutator<T extends Mutatable>
        extends AbstractMutator<T> {

    private List<AstNode> candidates;

    public AbstractReplacingAmongMutator(
            Class<? extends T> applicableClass, Collection<T> mutationTargets) {
        super(applicableClass);
        candidates = new ArrayList<AstNode>(mutationTargets.size());
        for (T attachment : mutationTargets) {
            candidates.add(getFocusedNode(attachment));
        }
    }

    /**
     * @return focus of Mutator object. If the class try to mutate event kind
     *         from event attachment statement like
     *         'element.addEventListener('click', func);', this method must
     *         return something like 'click'.
     */
    abstract protected AstNode getFocusedNode(T node);

    @Override
    public Mutation generateMutation(T originalNode) {
            Set<AstNode> improperNodes = new HashSet<AstNode>();
        AstNode focusedNode = getFocusedNode(originalNode);
        improperNodes.add(focusedNode);
        while (improperNodes.size() < candidates.size()) {
            AstNode candidate = candidates.get(Randomizer
                    .getInt(candidates.size()));
            if (isEqual(getFocusedNode(originalNode), candidate)
                    || include(originalNode.getAstNode(), candidate)
                    || include(candidate, originalNode.getAstNode())) {
                improperNodes.add(candidate);
            } else {
                return new Mutation(
                        focusedNode, formatAccordingTo(candidate, focusedNode));
            }
        }
        return null;
    }

    private boolean include(AstNode mayParent, AstNode mayChild) {
        if (mayParent == null || mayChild == null) {
            return false;
        }
        boolean parentStartsBeforeChild
                = mayParent.getAbsolutePosition() < mayChild.getAbsolutePosition();
        boolean parentEndsAfterChild
                = (mayParent.getAbsolutePosition() + mayParent.getLength())
                > (mayChild.getAbsolutePosition() + mayChild.getLength());
        return parentStartsBeforeChild && parentEndsAfterChild;
    }

    /**
     * This method is called to format String representation to be suitable for
     * mutation. For instance, if you try to request method in
     * <code>$.ajax(url, 'GET', callback);</code>, request method should be
     * string whereas our subclass try to get mutation candidate from
     * <code>$.post(url, callback);</code> where request method is a function
     * name. This method should be override to reformat the given
     * AstNode to a proper representation for mutation.
     * Default implementation returns mutatingNode#toSource().
     *
     * @param mutatingNode AstNode that will be used to replace existing node.
     * @param mutatedNode AstNode that will be mutated.
     * @return a proper string representation of mutatingNode.
     */
    protected String formatAccordingTo(
            AstNode mutatingNode, AstNode mutatedNode) {
        return mutatingNode.toSource();
    }
}
