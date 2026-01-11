package com.webx.tournaments.utils;

import java.util.List;

public class BracketGenerator {
    
    public static List<String> generateBracket(List<String> participants) {
        java.util.Collections.shuffle(participants);
        return participants;
    }
}
