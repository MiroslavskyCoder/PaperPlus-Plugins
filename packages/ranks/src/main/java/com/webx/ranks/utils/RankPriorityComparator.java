package com.webx.ranks.utils;

import com.webx.ranks.models.Rank;
import java.util.Comparator;

public class RankPriorityComparator implements Comparator<Rank> {
    @Override
    public int compare(Rank a, Rank b) {
        return Integer.compare(b.getPriority(), a.getPriority());
    }
}
