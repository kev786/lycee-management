package com.lycee.util;

import java.time.LocalDate;

public final class DateUtil {

    private DateUtil() {}

    /** Trimestre scolaire courant : T1 (sept-déc), T2 (jan-mars), T3 (avr-juin). */
    public static int getTrimestreCourant() {
        int month = LocalDate.now().getMonthValue();
        if (month >= 9) return 1;
        if (month <= 3) return 2;
        if (month <= 6) return 3;
        return 1;
    }
}
