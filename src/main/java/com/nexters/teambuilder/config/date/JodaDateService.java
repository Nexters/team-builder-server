package com.nexters.teambuilder.config.date;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class JodaDateService implements DateService {

    private final DateTimeZone timeZone;

    public JodaDateService(final DateTimeZone timeZone) {
        super();
        this.timeZone = checkNotNull(timeZone);

        System.setProperty("user.timezone", timeZone.getID());
        TimeZone.setDefault(timeZone.toTimeZone());
        DateTimeZone.setDefault(timeZone);
    }

    @Override
    public DateTime now() {
        return DateTime.now(timeZone);
    }
}
