package jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector;

import java.util.Set;

import jp.gr.java_conf.daisy.ajax_mutator.detector.AbstractDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.AttributeModification;

import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ElementGet;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.StringLiteral;

import com.google.common.collect.ImmutableSet;

/**
 * Detector for attribute modification such as element.id = newId and
 * element['id'] = newId
 * 
 * Note that we currently ignore this type of modification:
 * element.style.width = 10, 
 * element.style['width'] = 10
 *
 * @author Kazuki Nishiura
 */
public class AttributeAssignmentDetector 
		extends AbstractDetector<AttributeModification> {
	// TODO: we can infer variable type to detect if variable is DOM element.
	// Current implementation is:
	// If variable have property whose name is same as DOM property, 
	// (e.g., var.id) we regard such variable as DOM element.
	// 'If it behaves like a DOM, it must be a DOM'
	// refered http://www.w3schools.com/tags/ref_standardattributes.asp
	private final Set<String> globalAttributes = ImmutableSet.of(
			"accessKey", "class", "dir", 
			"id", "lang", "style", "tabindex", "title", 
			// listed bellow are HTML5 attributes
			"contenteditable", "contextmenu", "draggable", 
			"dropzone", "hidden", "spellcheck");
	
	// refered http://www.w3.org/TR/html4/index/attributes.html
	// Note that global attributes and on* attribute (event handlers) are omitted.
	private final Set<String> attributes = ImmutableSet.of(
			"abbr", "accept-charset", "accept",
			"action", "align", "alink", "alt", "archive", "axis", "background", 
			"bgcolor", "border", "cellpadding", "cellspacing", "char", "charoff", 
			"charset", "checked", "cite", "classid", "clear", "code", "codebase",
			"codetype", "color", "cols", "colspan", "compact", "content", "coords",
			"data", "datetime", "declare", "defer", "disabled", "enctype", "face",
			"for", "frame", "frameborder", "headers", "height", "href", "hreflang",
			"hspace", "http-equiv", "id", "ismap", "label", "language", 
			"link", "longdesc", "marginheight", "marginwidth", "maxlength", "media",
			"method", "multiple", "name", "nohref", "noresize", "noshade", "nowrap",
			"object", "profile", "prompt", "readonly", "rel", "rev", "rows", 
			"rowspan", "rules", "scheme", "scope", "scrolling", "selected", 
			"shape", "size", "span", "src", "standby", "start", "summary", "target", 
			"text", "type", "usemap", "valign", "value", "valuetype", "version", 
			"vlink", "vspace", "width");
	
	@Override
	public AttributeModification detect(AstNode node) {
		return detectFromAssignment(node, false);
	}
	
	@Override
	protected AttributeModification detectFromAssignment(Assignment assignment,
			AstNode left, AstNode right) {
		if (left instanceof PropertyGet) {
			AstNode target = ((PropertyGet) left).getTarget();
			Name property = ((PropertyGet) left).getProperty();
			if (mayHTMLAttribute(property)) {
				// elm.className = 'hoge'
				return new AttributeModification(
						assignment, target, property, right);
			}
		} else if (left instanceof ElementGet) {
			AstNode target = ((ElementGet) left).getTarget();
			AstNode element = ((ElementGet) left).getElement();
			if (element instanceof StringLiteral 
					&& mayHTMLAttribute(((StringLiteral) element).getValue())) {
				return new AttributeModification(
						assignment, target, element, right);
			}
		}
		return null;
	}
	
	private boolean mayHTMLAttribute(String property) {
		return globalAttributes.contains(property) || attributes.contains(property);
	}
	
	private boolean mayHTMLAttribute(Name name) {
		return mayHTMLAttribute(name.getIdentifier());
	}
}
