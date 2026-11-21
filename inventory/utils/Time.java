package inventory.utils;

import java.sql.Timestamp;

public class Time {
    public Timestamp getCurrentDate() { return new Timestamp(System.currentTimeMillis()); }
}
