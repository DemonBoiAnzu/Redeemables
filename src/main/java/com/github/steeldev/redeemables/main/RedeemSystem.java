package com.github.steeldev.redeemables.main;

import com.github.steeldev.redeemables.Redeemables;

public class RedeemSystem {
    static Redeemables main = Redeemables.getInstance();

    static String[] alpnum = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

    public enum RedeemState{
        SUCCESS,
        CODE_INVALID,
        CANT_BE_REDEEMED,
        ALREADY_REDEEMED,
        INVENTORY_FULL,
        ERROR
    }

    public static String generateRedeemCode(){
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < 12; i++) {
            String resAdd = alpnum[main.rand.nextInt(alpnum.length)];
            if(main.chanceOf(70)) resAdd = resAdd.toUpperCase();
            result.append(resAdd);
        }

        return result.insert(6,"-").insert(11,"-").toString();
    }
}
