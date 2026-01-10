package com.webx.miningevents.utils;

import com.webx.miningevents.models.MineEvent;

public class MiningUtils {
    
    public static int calculateOreAmount(int baseAmount, MineEvent event) {
        return (int) (baseAmount * event.getOreMultiplier());
    }
}
