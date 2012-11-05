package jp.gr.java_conf.daisy.ajax_mutator.mutatable;

import org.mozilla.javascript.ast.AstNode;

public class Request extends Mutatable {
	AstNode url;
	AstNode successHanlder;
	AstNode failureHandler;
	ResponseType responseType;
	AstNode parameters;

	public Request(AstNode node, AstNode url, AstNode successHandler, 
			AstNode failureHandler, ResponseType responseType, AstNode parameters) {
		super(node);
		this.url = url;
		this.successHanlder = successHandler;
		this.failureHandler = failureHandler;
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

	public enum ResponseType {
		JSON, TEXT, HTML
	}
}