package org.alcibiade.chess.persistence.pgn;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Tobias Boese <tobias.boese@gmail.com>
 */
public class PgnTagTest {
    @Test
    public void testOrder() {
        PgnTag eventTag = new EventTag();
        PgnTag siteTag = new SiteTag();
        PgnTag dateTag = new DateTag();

        List<PgnTag> tags = new ArrayList<>();
        tags.add(siteTag);
        tags.add(dateTag);
        tags.add(eventTag);

        Assertions.assertThat(tags.get(0)).isEqualTo(siteTag);
        Assertions.assertThat(tags.get(1)).isEqualTo(dateTag);
        Assertions.assertThat(tags.get(2)).isEqualTo(eventTag);

        Collections.sort(tags);

        Assertions.assertThat(tags.get(0)).isEqualTo(eventTag);
        Assertions.assertThat(tags.get(1)).isEqualTo(siteTag);
    }
}
