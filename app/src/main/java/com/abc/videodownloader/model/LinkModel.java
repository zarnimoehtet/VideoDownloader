package com.abc.videodownloader.model;

import java.util.StringTokenizer;

public class LinkModel {
    private String HD;
    private String SD;
    private String url;

    public LinkModel() {
    }

    public LinkModel(String url) {
        this.url = url;
    }

    public String getHD() {
        StringTokenizer st = new StringTokenizer(url,",");
        st.nextToken();
        String a = st.nextToken();

        return a;
    }

    public void setHD(String HD) {
        this.HD = HD;
    }

    public String getSD() {
        return SD;
    }

    public void setSD(String SD) {
        this.SD = SD;
    }


}
