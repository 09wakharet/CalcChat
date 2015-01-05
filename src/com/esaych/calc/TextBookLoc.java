package com.esaych.calc;

import android.widget.Toast;

public class TextBookLoc {

    private String chpt;
    private String sect;
    private String prob;

    public TextBookLoc(String chapter, String section, String problem) {
        chpt = chapter;
        sect = section;
        prob = problem;
    }

    public TextBookLoc(String parseName) {
        String[] dat = parseName.split(";");
        chpt = dat[0];
        sect = dat[1];
        prob = dat[2];
    }

    public void setChapter(String chapter) {
        chpt = chapter;
    }

    public String getChapter() {
        return chpt;
    }

    public void setSection(String section) {
        sect = section;
    }

    public String getSection() {
        return sect;
    }

    public void setProblem(String problem) {
        prob = problem;
    }

    public String getProblem() {
        return prob;
    }

    public String getURLForPageNum(ViewPage viewPage, int pageNum) {
        TextBookLoc temp = new TextBookLoc(toString());
        temp.setProblem((Integer.parseInt(prob) + pageNum*2)+"");
        return temp.getURL(viewPage);
    }

    public String getURL(ViewPage viewPage) {
        try {
            String linkChapter = ("00" + chpt);
            linkChapter = linkChapter.substring(linkChapter.length() - 2, linkChapter.length());
            String linkSection = "abcdefghhijklmnopqrstuvwxyz".charAt(Integer.parseInt(sect) - 1) + "";
            String linkProblem = ("000" + prob);
            linkProblem = linkProblem.substring(linkProblem.length() - 3, linkProblem.length());

            return "http://c811118.r18.cf2.rackcdn.com/se" + linkChapter + linkSection + "01" + linkProblem + ".gif";
        } catch (Exception e) {
            Toast.makeText(viewPage, "The problem you've entered does not exist.", Toast.LENGTH_LONG).show();
            return "http://i.imgur.com/uZyDWjl.png"; //DOGE Thinking
        }
    }

    public String toString() {
        return chpt + ";" + sect + ";" + prob;
    }

}
