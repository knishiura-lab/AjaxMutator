package jp.gr.java_conf.daisy.ajax_mutator;

import jp.gr.java_conf.daisy.ajax_mutator.detector.AbstractDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.EventAttacherDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector.TimerEventDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.*;

import java.util.Collections;
import java.util.Set;

public class MutateVisitorBuilder {
    private Set<EventAttacherDetector> eventAttacherDetectors
        = Collections.emptySet();
    private Set<TimerEventDetector> timerEventDetectors
        = Collections.emptySet();
    private Set<? extends AbstractDetector<DOMCreation>> domCreationDetectors
        = Collections.emptySet();
    private Set<? extends AbstractDetector<AttributeModification>> attributeModificationDetectors
            = Collections.emptySet();
    private Set<? extends AbstractDetector<DOMAppending>> domAppendingDetectors
            = Collections.emptySet();
    private Set<? extends AbstractDetector<DOMCloning>> domCloningDetectors
            = Collections.emptySet();
    private Set<? extends AbstractDetector<DOMRemoval>> domRemovalDetectors
            = Collections.emptySet();
    private Set<? extends AbstractDetector<DOMSelection>> domSelectionDetectors
            = Collections.emptySet();
    private Set<? extends AbstractDetector<Request>> requestDetectors
            = Collections.emptySet();

    protected MutateVisitorBuilder() {

    }

    public MutateVisitor build() {
        return new MutateVisitor(eventAttacherDetectors, timerEventDetectors,
                domCreationDetectors, attributeModificationDetectors,
                domAppendingDetectors, domCloningDetectors, domRemovalDetectors,
                domSelectionDetectors, requestDetectors);
    }

    public void setEventAttacherDetectors(
            Set<EventAttacherDetector> eventAttacherDetectors) {
        this.eventAttacherDetectors = eventAttacherDetectors;
    }

    public void setTimerEventDetectors(
            Set<TimerEventDetector> timerEventDetectors) {
        this.timerEventDetectors = timerEventDetectors;
    }

    public void setDomCreationDetectors(
            Set<? extends AbstractDetector<DOMCreation>> domCreationDetectors) {
        this.domCreationDetectors = domCreationDetectors;
    }

    public void setAttributeModificationDetectors(
            Set<? extends AbstractDetector<AttributeModification>> attributeModificationDetectors) {
        this.attributeModificationDetectors = attributeModificationDetectors;
    }

    public void setDomAppendingDetectors(
            Set<? extends AbstractDetector<DOMAppending>> domAppendingDetectors) {
        this.domAppendingDetectors = domAppendingDetectors;
    }

    public void setDomCloningDetectors(
            Set<? extends AbstractDetector<DOMCloning>> domCloningDetectors) {
        this.domCloningDetectors = domCloningDetectors;
    }

    public void setDomRemovalDetectors(
            Set<? extends AbstractDetector<DOMRemoval>> domRemovalDetectors) {
        this.domRemovalDetectors = domRemovalDetectors;
    }

    public void setDomSelectionDetectors(
            Set<? extends AbstractDetector<DOMSelection>> domSelectionDetectors) {
        this.domSelectionDetectors = domSelectionDetectors;
    }

    public void setRequestDetectors(
            Set<? extends AbstractDetector<Request>> requestDetectors) {
        this.requestDetectors = requestDetectors;
    }
}
