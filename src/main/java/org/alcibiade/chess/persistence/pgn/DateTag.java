package org.alcibiade.chess.persistence.pgn;

import org.alcibiade.chess.persistence.PgnFormats;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.alcibiade.chess.persistence.PgnFormats.DATEFORMAT_PGN;

/**
 * Created by b4dt0bi on 19.07.16.
 */
public class DateTag extends PgnTag {
    public DateTag() {
        super(TAG_ID_DATE);
    }

    public DateTag(final String date) {
        super(TAG_ID_DATE);
        setDate(date);
    }

    public DateTag(final Date date) {
        super(TAG_ID_DATE);
        setDate(date);
    }

    public void setDate(final String date) {
        value = (date == null || date.isEmpty()) ? "????.??.??" : date; // TODO : check date format?
    }

    public void setDate(final Date date) {
        SimpleDateFormat df = new SimpleDateFormat(PgnFormats.DATEFORMAT_PGN);
        value = (date == null) ? "????.??.??" : df.format(date);
    }

    public void setPartialDate(Integer year, Integer month, Integer day) {
        value = (year != null ? prependZero(year.toString(), 4) : "????") + "." +
                (month != null ? prependZero(month.toString(), 2) : "??") + "." +
                (day != null ? prependZero(day.toString(), 2) : "??");
    }

    private String prependZero(String text, int len) {
        String result = text;
        while (result.length() < len) {
            result = "0" + result;
        }
        return result;
    }

    public Date getDate() {
        if (value == null || value.isEmpty()) return null;
        SimpleDateFormat df = new SimpleDateFormat(DATEFORMAT_PGN);
        try {
            return df.parse(value.replaceAll("\\?\\?", "01"));
        } catch (ParseException e) {
            return null; // TODO : throw exception ?
        }
    }
}
