package com.abc.kzvideodownloader.utils;

public class LinkUtils {

    public static String HDlink = "";
    public static String SDLink = "";

    public static String isSDLink(String url) {

        String pone;
        pone = "/sd_src_no_ratelimit:\"([^\"]+)\"/";
        String ptwo;
        ptwo = "/sd_src:\"([^\"]+)\"/";

        if (url.matches(pone)) {
            return "";
        } else if (url.matches(ptwo)) {
            return "";
        } else {
            return "";
        }
    }

    public static boolean isHDLink(String url) {

        String pone = "/hd_src_no_ratelimit:'([^']+)'/";
        String ptwo = "/hd_src:'([^']+)'/";

        if (url.matches(pone) || url.matches(ptwo)) {
            return true;
        } else {
            return false;
        }
    }


    public static String getHDlink() {
        return HDlink;
    }

    public static String getSDLink() {
        return SDLink;
    }

}
