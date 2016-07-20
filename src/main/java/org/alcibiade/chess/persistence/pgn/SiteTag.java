package org.alcibiade.chess.persistence.pgn;

/**
 * Created by b4dt0bi on 19.07.16.
 */
public class SiteTag extends PgnTag {
    public SiteTag() {
        super(TAG_ID_SITE);
    }

    public SiteTag(final String site) {
        super(TAG_ID_SITE);
        setSite(site);
    }

    public void setSite(final String site) {
        value = (site == null || site.isEmpty()) ? "?" : site;
    }
}
