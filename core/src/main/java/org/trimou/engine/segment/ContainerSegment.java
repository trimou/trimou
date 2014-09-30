package org.trimou.engine.segment;

import java.util.List;

import org.trimou.annotations.Internal;

/**
 * Segment which contains other segments.
 *
 * @author Martin Kouba
 */
@Internal
public interface ContainerSegment extends Segment, Iterable<Segment> {

    /**
     *
     * @return the immutable list of segments
     */
    List<Segment> getSegments();

    /**
     *
     * @param recursive
     * @return the number of segments
     */
    int getSegmentsSize(boolean recursive);

    /**
     *
     * @return the reconstructed literal block this segment contains (original
     *         text before compilation)
     */
    String getContentLiteralBlock();

}
