package com.lksnext.parkingalaiat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EmailGenerator {

    public static String generateUniqueEmail() {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
        return "user" + timeStamp + "@example.com";
    }
}
