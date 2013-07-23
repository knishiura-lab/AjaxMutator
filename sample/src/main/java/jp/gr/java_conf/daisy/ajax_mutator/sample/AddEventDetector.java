package jp.gr.java_conf.daisy.ajax_mutator.sample;

import java.util.List;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.Name;

import jp.gr.java_conf.daisy.ajax_mutator.detector.EventAttacherDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.EventAttachment;

/**
 * Implementation of {@link EventAttacherDetector} to deal with user function
 * like "addEvent(target, event, callback)". Users of AjaxMutator can capture
 * their own wrapper function by implementing user class like this.
 * 
 * @author Kazuki Nishiura
 */
public class AddEventDetector extends EventAttacherDetector {
    private static final String targetString = "addEvent";

    @Override
    public EventAttachment detectFromFunctionCall(FunctionCall functionCall,
            AstNode target, List<AstNode> arguments) {
        if (target instanceof Name
                && targetString.equals(((Name) target).getIdentifier())) {
            return new EventAttachment(functionCall, arguments.get(0),
                    arguments.get(1), arguments.get(2));
        }
        return null;
    }
}
