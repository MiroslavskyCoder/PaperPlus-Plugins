package com.webx.ranks.utils;

import com.webx.ranks.models.Rank;

public class RankUtils {
    public static int comparePriority(Rank a, Rank b) {
        return Integer.compare(b.getPriority(), a.getPriority());
    }
    // Прочие утилиты
}
