package com.faltenreich.diaguard.adapter.list;

/**
 * Created by Faltenreich on 26.03.2017
 */

public class ListItemChangelog extends ListItem {

    private String text;

    public ListItemChangelog(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}