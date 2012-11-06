package test.jp.gr.java_conf.daisy.ajax_mutator;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector.DOMManipulationDetectorTest;
import test.jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector.EventDetectorTest;
import test.jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.JQueryDetectorTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	DOMManipulationDetectorTest.class,
	EventDetectorTest.class,
	JQueryDetectorTest.class
})
public class TestAll {

}
