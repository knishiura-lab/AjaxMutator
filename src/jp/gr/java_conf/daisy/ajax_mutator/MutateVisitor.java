package jp.gr.java_conf.daisy.ajax_mutator;

import java.util.Set;
import java.util.TreeSet;

import jp.gr.java_conf.daisy.ajax_mutator.detector.AbstractDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.EventAttacherDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.MutationPointDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector.TimerEventDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.AttributeModification;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMCreation;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMRemoval;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMSelection;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.EventAttachment;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Mutatable;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Request;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.TimerEventAttachment;

import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.NodeVisitor;

import com.google.common.collect.ImmutableSet;

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
    private final Set<DOMRemoval> domRemovals = new TreeSet<DOMRemoval>();
    private final Set<DOMSelection> domSelections = new TreeSet<DOMSelection>();
    private final Set<Request> requests = new TreeSet<Request>();

    public MutateVisitor(
            Set<EventAttacherDetector> eventAttacherDetectors,
            Set<TimerEventDetector> timerEventDetectors,
            Set<? extends AbstractDetector<DOMCreation>> domCreationDetectors,
            Set<? extends AbstractDetector<AttributeModification>> attributeModificationDetectors,
            Set<? extends AbstractDetector<DOMRemoval>> domRemovalDetectors,
            Set<? extends AbstractDetector<DOMSelection>> domSelectionDetectors,
            Set<? extends AbstractDetector<Request>> requestDetectors) {
        this.eventAttacherDetectors = immutableCopyOf(eventAttacherDetectors);
        this.timerEventDetectors = immutableCopyOf(timerEventDetectors);
        this.domCreationDetectors = immutableCopyOf(domCreationDetectors);
        this.attributeModificationDetectors = immutableCopyOf(attributeModificationDetectors);
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

    public Set<DOMRemoval> getDomRemovals() {
        return domRemovals;
    }

    public Set<DOMSelection> getDomSelections() {
        return domSelections;
    }

    public Set<Request> getRequests() {
        return requests;
    }

    private <T> void appendSet(String title, StringBuilder builder, Set<T> set) {
        builder.append("  --- ").append(title);
        builder.append(" (").append(set.size()).append(") ---\n");
        for (T element : set) {
            String str = element.toString();
            String spaceBeforeContent = "    ";
            builder.append(spaceBeforeContent)
                    .append(str.replaceAll("\n", "\n" + spaceBeforeContent))
                    .append('\n');
        }
    }

    public String MutatablesInfo() {
        StringBuilder builder = new StringBuilder();
        builder.append("=== Event ===\n");
        appendSet("Event attachments", builder, eventAttachments);
        appendSet("Timer event attachment", builder,
                timerEventAttachmentExpressions);
        builder.append("=== DOM ===\n");
        appendSet("DOM creation", builder, domCreations);
        appendSet("Attribute modification", builder, attributeModifications);
        appendSet("DOM removal", builder, domRemovals);
        appendSet("DOM Selection", builder, domSelections);
        builder.append("=== Asynchrous communications ===\n");
        appendSet("Requests", builder, requests);

        return builder.toString();
    }
}