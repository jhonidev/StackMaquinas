package net.stackmaquinas.plugin.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class FormatMoney {

    private static final Pattern FORMAT_PATTERN = Pattern.compile("^\\d{1,3}([a-zA-Z]+|\\.\\d{1,2}[\\D]+$)");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("((^\\d{1,3})([.][\\d+]{1,2})?)(\\D+$)?");

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###.##");
    private static final List<String> FORMATS = Arrays.asList("","K","M","B","T","Q","QQ","S","SS","OC","N","D","UN","DD","TR","QT","QN");

    public static String format(double number, boolean opt) {
        int base = (int) Math.log10(number);
        int index = base / 3;

        if(index < 0) {
            index = 0;
        }

        number = (number / Math.pow(10, index * 3));

        String symbol = index < FORMATS.size() ? FORMATS.get(index) : "";
        return DECIMAL_FORMAT.format(number) + symbol;
    }

}
