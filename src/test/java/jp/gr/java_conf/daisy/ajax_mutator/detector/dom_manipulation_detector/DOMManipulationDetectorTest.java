package jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.*;
import org.junit.Test;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.StringLiteral;

import static jp.gr.java_conf.daisy.ajax_mutator.util.StringToAst.parseAsAssignment;
import static jp.gr.java_conf.daisy.ajax_mutator.util.StringToAst.parseAsFunctionCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DOMManipulationDetectorTest {
    @Test
    public void testAppendChildDetector() {
        AppendChildDetector detector = new AppendChildDetector();

        DOMAppending result
            = detector.detect(parseAsFunctionCall("hoge.appendChild(fuga);"));
        assertTrue(result != null);
        assertEquals("hoge", ((Name) result.getAppendTarget()).getIdentifier());
        assertEquals("fuga", ((Name) result.getAppendedDom()).getIdentifier());
    }

    @Test
    public void removeChildDetectorTest() {
        RemoveChildDetector detector = new RemoveChildDetector();

        DOMRemoval result
            = detector.detect(parseAsFunctionCall("hoge.removeChild(fuga);"));
        assertTrue(result != null);
        assertEquals("hoge", ((Name) result.getFrom()).getIdentifier());
        assertEquals("fuga", ((Name) result.getTarget()).getIdentifier());
    }

    @Test
    public void cloneElementDetectorTest() {
        CloneNodeDetector detector = new CloneNodeDetector();
        DOMCloning result
                = detector.detect(parseAsFunctionCall("element.cloneNode(false);"));
        assertTrue(result != null);
        assertEquals("element", result.getTargetNode().toSource());
    }

    @Test
    public void craeteElementDetectorTest() {
        CreateElementDetector detector = new CreateElementDetector();
        DOMCreation result
            = detector.detect(parseAsFunctionCall("document.createElement('div')"));
        assertTrue(result != null);
        assertEquals("div", ((StringLiteral) result.getTagName()).getValue());
    }

    @Test
    public void domSelectionDetectorTest() {
        DOMSelectionDetector detector = new DOMSelectionDetector();
        DOMSelection result
            = detector.detect(parseAsFunctionCall("document.getElementById('aaa')"));
        assertTrue(result != null);
        assertEquals(DOMSelection.SelectionMethod.ID,
                result.getSelectionMethod());
        assertTrue(result.getRange() != null);
        AstNode selector = result.getSelector();
        assertEquals("'aaa'", selector.toSource());
    }

    @Test
    public void attributeAssignmentDetectorTest() {
        AttributeAssignmentDetector detector = new AttributeAssignmentDetector();
        AttributeModification result
            = detector.detect(parseAsAssignment("hoge.hidden = true;"));
        assertTrue(result != null);
        assertEquals("true", result.getAttributeValue().toSource());
        assertEquals("hoge", result.getTargetDom().toSource());
        assertEquals("hidden", result.getTargetAttribute().toSource());
        result = detector.detect(parseAsAssignment("hoge.hige = true;"));
        assertTrue(result == null);
        result = detector.detect(parseAsAssignment("document.findElementById('hoge')['hidden'] = true;"));
        assertTrue(result != null);
    }

    @Test
    public void replaceChildDetectorTest() {
        ReplaceChildDetector detector = new ReplaceChildDetector();
        DOMReplacement result
                = detector.detect(parseAsFunctionCall("elm.replaceChild(foo, elm.childNodes[0])"));
        assertTrue(result != null);
        assertEquals("foo", ((Name) result.getReplacedNode()).getIdentifier());
        assertEquals("elm.childNodes[0]", result.getReplacingNode().toSource());
    }
}
