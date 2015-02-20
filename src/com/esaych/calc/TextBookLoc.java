package com.esaych.calc;

import android.widget.Toast;
/**
 * Created by Samuel Holmberg on 2/17/2015.
 * This class is designed to be the basic location object of problems in the text book based on chapter, section and problem
 */
public class TextBookLoc {

    private int chpt;
    private int sect;
    private int prob;

    public TextBookLoc(int chapter, int section, int problem) {
        chpt = chapter;
        sect = section;
        prob = problem;
    }

    public TextBookLoc(String parseName) {
        String[] dat = parseName.split(";");
        chpt = Integer.parseInt(dat[0]);
        sect = Integer.parseInt(dat[1]);
        prob = Integer.parseInt(dat[2]);
    }

    public int getChapter() {
        return chpt;
    }

    public int getSection() {
        return sect;
    }

    public int getProblem() {
        return prob;
    }

    public TextBookLoc offSet(int amount) {
        return new TextBookLoc(chpt, sect, (prob+amount*2));
    }

    public String getURL(ViewPage viewPage) {
        try {
            String linkChapter = ("00" + chpt);
            linkChapter = linkChapter.substring(linkChapter.length() - 2, linkChapter.length());
            String linkSection = "abcdefghhijklmnopqrstuvwxyz".charAt(sect - 1) + "";
            String linkProblem = ("000" + prob);
            linkProblem = linkProblem.substring(linkProblem.length() - 3, linkProblem.length());

            return "http://c811118.r18.cf2.rackcdn.com/se" + linkChapter + linkSection + "01" + linkProblem + ".gif";
        } catch (Exception e) {
//            Toast.makeText(viewPage, "The problem you've entered does not exist.", Toast.LENGTH_LONG).show();
            return "http://i.imgur.com/uZyDWjl.png"; //DOGE Thinking
        }
    }

    public TextBookLoc getNextProb() {
        return new TextBookLoc(chpt, sect, (prob+2));
    }

    public TextBookLoc getPrevProb() {
        return new TextBookLoc(chpt, sect, (prob-2));
    }

    public String toString() {
        return chpt + ";" + sect + ";" + prob;
    }

    public boolean equals(Object o) {
        if (!(o instanceof TextBookLoc))
            return false;
        TextBookLoc loc = ((TextBookLoc) o);
        if (loc.getProblem() == prob)
            if (loc.getSection() == sect)
                if (loc.getChapter() == chpt)
                    return true;
        return false;
    }

}
