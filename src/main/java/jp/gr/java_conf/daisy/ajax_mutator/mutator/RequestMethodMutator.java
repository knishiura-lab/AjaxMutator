package jp.gr.java_conf.daisy.ajax_mutator.mutator;

import com.google.common.collect.ImmutableSet;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Request;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Request.RequestMethod;
import org.mozilla.javascript.ast.AstNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * {@link Mutator} for replacing http request method (e.g., get, post)
 *
 * @author Kazuki Nishiura
 */
public class RequestMethodMutator implements Mutator {
    private int indexOfTarget = 0;
    private List<Request> mutationTargets;
    private Request lastMutationTarget;
    private RequestMethod lastRequestMethod;

    public RequestMethodMutator(Collection<Request> mutationTargets) {
        this.mutationTargets = new ArrayList<Request>(mutationTargets);
    }

    @Override
    public String applyMutation() {
        lastMutationTarget = mutationTargets.get(indexOfTarget);
        indexOfTarget++;

        switch (lastMutationTarget.getRequestMethod()) {
            case UNKNOWN:
                return null;
            case GET:
                return applyMethodReplace(
                        lastMutationTarget, RequestMethod.POST);
            default:
                return applyMethodReplace(
                        lastMutationTarget, RequestMethod.GET);
        }
    }

    private String applyMethodReplace(Request request, RequestMethod method) {
        lastRequestMethod = request.getRequestMethod();
        request.replaceMethod(method);
        return new StringBuilder()
                .append("Mutate request method from ")
                .append(lastRequestMethod)
                .append(" to ")
                .append(method)
                .toString();
    }

    @Override
    public void undoMutation() {
        lastMutationTarget.replaceMethod(lastRequestMethod);
    }

    @Override
    public void skipMutation() {
        indexOfTarget++;
    }

    @Override
    public boolean isFinished() {
        return indexOfTarget >= mutationTargets.size();
    }

    @Override
    public int numberOfMutation() {
        return mutationTargets.size();
    }

    @Override
    public String mutationName() {
        return "RequestMethodMutation";
    }
}
