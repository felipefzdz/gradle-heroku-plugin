package com.felipefzdz.gradle.heroku.utils

import com.google.common.base.CaseFormat

class FormatUtil {

    public static String toUpperCamel(String lowerHyphen) {
        CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, lowerHyphen)
    }
}
