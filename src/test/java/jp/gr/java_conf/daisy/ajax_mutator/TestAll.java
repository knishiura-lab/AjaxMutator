package jp.gr.java_conf.daisy.ajax_mutator;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector.DOMManipulationDetectorTest;
import jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector.EventDetectorTest;
import jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.JQueryDetectorTest;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.AttributeModificationMutatorTest;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.EventMutatorTest;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.RequestMutatorTest;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.TimerEventMutatorTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ DOMManipulationDetectorTest.class,
        EventDetectorTest.class, JQueryDetectorTest.class,
        AttributeModificationMutatorTest.class, EventMutatorTest.class,
        RequestMutatorTest.class, TimerEventMutatorTest.class})
public class TestAll {

}
