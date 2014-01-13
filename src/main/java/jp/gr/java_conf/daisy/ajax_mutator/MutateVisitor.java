package jp.gr.java_conf.daisy.ajax_mutator;

import com.google.common.collect.ImmutableSet;
import jp.gr.java_conf.daisy.ajax_mutator.detector.AbstractDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.EventAttacherDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.MutationPointDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector.*;
import jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector.AddEventListenerDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector.AttachEventDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector.TimerEventDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.*;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.*;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.NodeVisitor;

import java.util.Set;
import java.util.TreeSet;

/**
 * Visitor for JavaScript's AST to get information needed to apply mutation
 * operations.
 *
 * @author Kazuki Nishiura
 */
public class MutateVisitor implements NodeVisitor {
    private final ImmutableSet<EventAttacherDetector> eventAttacherDetectors;
    private final ImmutableSet<TimerEventDetector> timerEventDetectors;
    private final ImmutableSet<? extends AbstractDetector<DOMCreation>> domCreationDetectors;
    private final ImmutableSet<? extends AbstractDetector<AttributeModification>> attributeModificationDetectors;
    private final ImmutableSet<? extends AbstractDetector<DOMAppending>> domAppendingDetectors;
    private final ImmutableSet<? extends AbstractDetector<DOMCloning>> domCloningDetectors;
    private final ImmutableSet<? extends AbstractDetector<DOMRemoval>> domRemovalDetectors;
    private final ImmutableSet<? extends AbstractDetector<DOMSelection>> domSelectionDetectors;
    private final ImmutableSet<? extends AbstractDetector<Request>> requestDetectors;

    private final Set<EventAttachment> eventAttachments
        = new TreeSet<EventAttachment>();
    private final Set<TimerEventAttachment> timerEventAttachmentExpressions
        = new TreeSet<TimerEventAttachment>();
    private final Set<DOMCreation> domCreations = new TreeSet<DOMCreation>();
    private final Set<AttributeModification> attributeModifications
        = new TreeSet<AttributeModification>();
    private final Set<DOMAppending> domAppendings = new TreeSet<DOMAppending>();
    private final Set<DOMCloning> domClonings = new TreeSet<DOMCloning>();
    private final Set<DOMRemoval> domRemovals = new TreeSet<DOMRemoval>();
    private final Set<DOMSelection> domSelections = new TreeSet<DOMSelection>();
    private final Set<Request> requests = new TreeSet<Request>();

    public MutateVisitor(
            Set<EventAttacherDetector> eventAttacherDetectors,
            Set<TimerEventDetector> timerEventDetectors,
            Set<? extends AbstractDetector<DOMCreation>> domCreationDetectors,
            Set<? extends AbstractDetector<AttributeModification>> attributeModificationDetectors,
            Set<? extends AbstractDetector<DOMAppending>> domAppendingDetectors,
            Set<? extends AbstractDetector<DOMCloning>> domCloningDetectors,
            Set<? extends AbstractDetector<DOMRemoval>> domRemovalDetectors,
            Set<? extends AbstractDetector<DOMSelection>> domSelectionDetectors,
            Set<? extends AbstractDetector<Request>> requestDetectors) {
        this.eventAttacherDetectors = immutableCopyOf(eventAttacherDetectors);
        this.timerEventDetectors = immutableCopyOf(timerEventDetectors);
        this.domCreationDetectors = immutableCopyOf(domCreationDetectors);
        this.attributeModificationDetectors = immutableCopyOf(attributeModificationDetectors);
        this.domAppendingDetectors = immutableCopyOf(domAppendingDetectors);
        this.domCloningDetectors = immutableCopyOf(domCloningDetectors);
        this.domRemovalDetectors = immutableCopyOf(domRemovalDetectors);
        this.domSelectionDetectors = immutableCopyOf(domSelectionDetectors);
        this.requestDetectors = immutableCopyOf(requestDetectors);
    }

    private <T> ImmutableSet<T> immutableCopyOf(Set<T> original) {
        if (original == null)
            return ImmutableSet.of();
        else
            return ImmutableSet.copyOf(original);
    }

    private <T extends Mutatable> void detectAndAdd(
            MutationPointDetector<T> detector, AstNode node, Set<T> mutatable) {
        T result = detector.detect(node);
        if (result != null)
            mutatable.add(result);
    }

    @Override
    public boolean visit(AstNode node) {
        if (node instanceof FunctionCall) {
            return visit((FunctionCall) node);
        } else if (node instanceof Assignment) {
            for (AbstractDetector<AttributeModification> detector : attributeModificationDetectors)
                detectAndAdd(detector, node, attributeModifications);
        }
        return true;
    }

    public boolean visit(FunctionCall call) {
        for (EventAttacherDetector detector : eventAttacherDetectors)
            detectAndAdd(detector, call, eventAttachments);
        for (TimerEventDetector detector : timerEventDetectors)
            detectAndAdd(detector, call, timerEventAttachmentExpressions);
        for (AbstractDetector<DOMCreation> detector : domCreationDetectors)
            detectAndAdd(detector, call, domCreations);
        for (AbstractDetector<DOMAppending> detector: domAppendingDetectors)
            detectAndAdd(detector, call, domAppendings);
        for (AbstractDetector<DOMCloning> detector: domCloningDetectors)
            detectAndAdd(detector, call, domClonings);
        for (AbstractDetector<DOMRemoval> detector : domRemovalDetectors)
            detectAndAdd(detector, call, domRemovals);
        for (AbstractDetector<DOMSelection> detector : domSelectionDetectors)
            detectAndAdd(detector, call, domSelections);
        for (AbstractDetector<Request> detector : requestDetectors)
            detectAndAdd(detector, call, requests);
        for (AbstractDetector<AttributeModification> detector : attributeModificationDetectors)
            detectAndAdd(detector, call, attributeModifications);
        return true;
    }

    public Set<EventAttachment> getEventAttachments() {
        return eventAttachments;
    }

    public Set<TimerEventAttachment> getTimerEventAttachmentExpressions() {
        return timerEventAttachmentExpressions;
    }

    public Set<DOMCreation> getDomCreations() {
        return domCreations;
    }

    public Set<AttributeModification> getAttributeModifications() {
        return attributeModifications;
    }

    public Set<DOMAppending> getDomAppendings() {
        return domAppendings;
    }

    public Set<DOMCloning> getDomClonings() {
        return domClonings;
    }

    public Set<DOMRemoval> getDomRemovals() {
        return domRemovals;
    }

    public Set<DOMSelection> getDomSelections() {
        return domSelections;
    }

    public Set<Request> getRequests() {
        return requests;
    }

    /**
     * @return Information about mutatables found by visiting AST.
     */
    public String getMutatablesInfo() {
        return getMutatablesInfo(true);
    }

    /**
     * @param detailed if true output detailed information about each mutant,
     *                 otherwise output value only contain number of each mutants
     * @return Information about mutatables found by visiting AST.
     */
    public String getMutatablesInfo(boolean detailed) {
        StringBuilder builder = new StringBuilder();
        builder.append("=== Event ===").append(System.lineSeparator());
        appendMutatablesInfo(
                "Event attachments", builder, eventAttachments, detailed);
        appendMutatablesInfo("Timer event attachment", builder,
                timerEventAttachmentExpressions, detailed);
        builder.append("=== DOM ===").append(System.lineSeparator());
        appendMutatablesInfo("DOM creation", builder, domCreations, detailed);
        appendMutatablesInfo("Attribute modification", builder,
                attributeModifications, detailed);
        appendMutatablesInfo("DOM removal", builder, domRemovals, detailed);
        appendMutatablesInfo("DOM Selection", builder, domSelections, detailed);
        builder.append("=== Asynchrous communications ===")
                .append(System.lineSeparator());
        appendMutatablesInfo("Requests", builder, requests, detailed);

        return builder.toString();
    }

    private <T> void appendMutatablesInfo(
            String title, StringBuilder builder, Set<T> set, boolean detailed) {
        builder.append("  --- ").append(title);
        builder.append(" (").append(set.size()).append(") ---")
                .append(System.lineSeparator());
        if (!detailed) {
            return;
        }
        for (T element : set) {
            String str = element.toString();
            String spaceBeforeContent = "    ";
            builder.append(spaceBeforeContent)
                    .append(str.replaceAll("\n", "\n" + spaceBeforeContent))
                    .append(System.lineSeparator());
        }
    }

    /**
     * @return Builder instance with no configuration.
     */
    public static MutateVisitorBuilder emptyBuilder() {
        return new MutateVisitorBuilder();
    }

    /**
     * @return Builder instance with typical detector configurations.
     */
    public static MutateVisitorBuilder defaultBuilder() {
        MutateVisitorBuilder builder = new MutateVisitorBuilder();
        builder.setAttributeModificationDetectors(
                ImmutableSet.of(new AttributeAssignmentDetector(), new SetAttributeDetector()));
        builder.setDomAppendingDetectors(
                ImmutableSet.of(new AppendChildDetector()));
        builder.setDomCreationDetectors(
                ImmutableSet.of(new CreateElementDetector()));
        builder.setDomRemovalDetectors(
                ImmutableSet.of(new RemoveChildDetector()));
        builder.setDomSelectionDetectors(
                ImmutableSet.of(new DOMSelectionDetector()));
        builder.setEventAttacherDetectors(
                ImmutableSet.<EventAttacherDetector>of(
                        new AddEventListenerDetector(), new AttachEventDetector()));
        builder.setTimerEventDetectors(
                ImmutableSet.of(new TimerEventDetector()));
        return builder;
    }

    public static MutateVisitorBuilder defaultJqueryBuilder() {
        MutateVisitorBuilder builder = new MutateVisitorBuilder();
        builder.setAttributeModificationDetectors(
                ImmutableSet.of(
                        new AttributeAssignmentDetector(), new SetAttributeDetector(),
                        new JQueryAttributeModificationDetector()));
        builder.setDomAppendingDetectors(
                ImmutableSet.of(new JQueryAppendDetector()));
        builder.setDomCreationDetectors(
                ImmutableSet.of(new CreateElementDetector()));
        builder.setDomRemovalDetectors(
                ImmutableSet.of(new RemoveChildDetector(), new JQueryRemoveDetector()));
        builder.setDomSelectionDetectors(
                ImmutableSet.of(new JQueryDOMSelectionDetector()));
        builder.setEventAttacherDetectors(
                ImmutableSet.<EventAttacherDetector>of(
                        new JQueryEventAttachmentDetector()));
        builder.setTimerEventDetectors(
                ImmutableSet.of(new TimerEventDetector()));
        builder.setRequestDetectors(
                ImmutableSet.of( new JQueryRequestDetector()));
        return builder;
    }
}