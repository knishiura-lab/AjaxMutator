package jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector;

import java.util.List;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.Name;

import jp.gr.java_conf.daisy.ajax_mutator.detector.EventAttacherDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.EventAttachment;

/**
 * concrete EventAttachementDetector to detect "addEvent(target, event,
 * callback)
 * 
 * @author Kazuki Nishiura
 */
public class AddEventDetector extends EventAttacherDetector {
    private static String targetString = "addEvent";

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
