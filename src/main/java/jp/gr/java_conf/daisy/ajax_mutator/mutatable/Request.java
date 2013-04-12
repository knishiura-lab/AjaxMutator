package jp.gr.java_conf.daisy.ajax_mutator.mutatable;

import jp.gr.java_conf.daisy.ajax_mutator.util.Util;

import org.mozilla.javascript.ast.AstNode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Request extends Mutatable {
    AstNode url;
    AstNode successHanlder;
    AstNode failureHandler;
    AstNode methodNode;
    RequestMethod requestMethod;
    ResponseType responseType;
    AstNode parameters;

    public Request(AstNode node, AstNode url, AstNode successHandler,
            AstNode failureHandler, AstNode methodNode,
            ResponseType responseType, AstNode parameters) {
        super(node);
        this.url = url;
        this.successHanlder = successHandler;
        this.failureHandler = failureHandler;
        this.methodNode = methodNode;
        requestMethod = parseRequestMethod(methodNode);
        this.responseType = responseType;
        this.parameters = parameters;
    }

    public AstNode getUrl() {
        return url;
    }

    public AstNode getSuccessHanlder() {
        return successHanlder;
    }

    public AstNode getFailureHandler() {
        return failureHandler;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public AstNode getParameters() {
        return parameters;
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public void replaceUrl(AstNode newUrl) {
        replace(url, newUrl);
    }

    public void replaceOnSuccessCallback(AstNode newCallback) {
        replace(successHanlder, newCallback);
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(super.toString()).append('\n')
                .append("  Request (").append(requestMethod).append("): [url:")
                .append(url).append(", onSuccess:")
                .append(Util.oneLineStringOf(successHanlder)).append("]")
                .toString();
    }

    public enum ResponseType {
        JSON, TEXT, HTML
    }

    public enum RequestMethod {
        GET, POST, PUT, HEAD, DELETE,
        /* We cannot know what request method is used if it is
           specified as variable and will be determined at run-time. */
        UNKNOWN
    }

    private RequestMethod parseRequestMethod(AstNode requestMethodNode) {
        if (requestMethodNode == null) {
            return RequestMethod.UNKNOWN;
        }

        String str = requestMethodNode.toSource().toUpperCase();
        Set<RequestMethod> knownMethods = new HashSet<RequestMethod>(
                Arrays.asList(RequestMethod.values()));
        knownMethods.remove(RequestMethod.UNKNOWN);
        for (RequestMethod method: knownMethods) {
            if (str.contains(method.name())) {
                return method;
            }
        }
        return RequestMethod.UNKNOWN;
    }
}