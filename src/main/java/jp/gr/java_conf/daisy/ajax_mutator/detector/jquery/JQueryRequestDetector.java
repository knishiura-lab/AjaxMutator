package jp.gr.java_conf.daisy.ajax_mutator.detector.jquery;

import java.util.List;
import java.util.Set;

import jp.gr.java_conf.daisy.ajax_mutator.detector.AbstractDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Request;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Request.ResponseType;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.ObjectLiteral;
import org.mozilla.javascript.ast.ObjectProperty;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.StringLiteral;

import com.google.common.collect.ImmutableSet;

/**
 * Detector that detect jQuery's requests, which include $.ajax(url, param),
 * $.get(url, [,data] [,callback] [,dataType]).
 *
 * currently we assume that data or param is passed as a literal, not a
 * variable.
 *
 * @author Kazuki Nishiura
 */
public class JQueryRequestDetector extends AbstractDetector<Request> {
    private static final String AJAX_METHOD = "ajax";
    private static final Set<String> AJAX_SHORTCUT_METHODS
        = ImmutableSet.of("get", "post", "getJSON");

    private AstNode successHandler;
    private AstNode failureHandler;
    private AstNode requestMethodNode;
    private Request.ResponseType responseType;
    private AstNode data;
    private AstNode url;

    @Override
    public Request detect(AstNode node) {
        return detectFromFunctionCall(node, true);
    }

    @Override
    public Request detectFromFunctionCall(FunctionCall functionCall,
            AstNode target, List<AstNode> arguments) {
        resetParams();
        if (target instanceof PropertyGet) {
            PropertyGet properyGet = (PropertyGet) target;
            AstNode obj = properyGet.getTarget();
            Name methodName = properyGet.getProperty();
            String methodNameString = properyGet.getProperty().getIdentifier();
            if (obj instanceof Name && "$".equals(((Name) obj).getIdentifier())) {
                if (AJAX_METHOD.equals(methodNameString)) {
                    ObjectLiteral settings = null;
                    if (arguments.get(0) instanceof ObjectLiteral) {
                        settings = (ObjectLiteral) arguments.get(0);
                    } else if (arguments.size() > 1
                            && arguments.get(1) instanceof ObjectLiteral) {
                        settings = (ObjectLiteral) arguments.get(1);
                        url = arguments.get(0);
                    }

                    if (settings != null) {
                        parseParams(settings);
                        return new Request(functionCall, url, successHandler,
                                failureHandler, requestMethodNode, responseType,
                                data);
                    }
                } else if (AJAX_SHORTCUT_METHODS.contains(methodNameString)) {
                    if (arguments.get(1) instanceof ObjectLiteral) {
                        ObjectLiteral settings
                            = (ObjectLiteral) arguments.get(1);
                        parseParams(settings);
                        if (arguments.size() > 2)
                            successHandler = arguments.get(2);
                    } else if (arguments.size() > 1) {
                        successHandler = arguments.get(1);
                    }

                    if ("getJSON".equals(methodNameString))
                        responseType = ResponseType.JSON;

                    return new Request(functionCall, arguments.get(0),
                            successHandler, failureHandler, methodName,
                            responseType, data);
                }
            }
        }
        return null;
    }

    private void resetParams() {
        successHandler = null;
        failureHandler = null;
        responseType = ResponseType.TEXT;
        data = null;
        url = null;
    }

    private void parseParams(ObjectLiteral params) {
        data = params;
        for (ObjectProperty property : params.getElements()) {
            AstNode left = property.getLeft();
            String leftInStr = null;
            if (left instanceof StringLiteral) {
                leftInStr = ((StringLiteral) left).getValue();
            } else if (left instanceof Name) {
                leftInStr = ((Name) left).getIdentifier();
            } else {
                continue;
            }
            AstNode right = property.getRight();

            if ("data".equals(leftInStr)) {
                data = right;
            } else if ("success".equals(leftInStr)) {
                successHandler = right;
            } else if ("error".equals(leftInStr)) {
                failureHandler = right;
            } else if ("url".equals(leftInStr)) {
                url = right;
            } else if ("dataType".equals(leftInStr)) {
                if (right instanceof StringLiteral) {
                    String type = ((StringLiteral) right).getValue().trim();

                    if ("html".equals(type))
                        responseType = ResponseType.HTML;
                    else if ("json".equals(type))
                        responseType = ResponseType.JSON;
                }
            } else if ("type".equals(leftInStr)) {
              requestMethodNode = right;
            }
        }
    }
}
