package com.github.steeldev.redeemables.main;

import com.github.steeldev.redeemables.Redeemables;
import jdk.internal.jline.internal.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RedeemSystem {
    static Redeemables main = Redeemables.getInstance();

    static String[] alpnum = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

    public static String generateRedeemCode() {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < 12; i++) {
            String resAdd = alpnum[main.rand.nextInt(alpnum.length)];
            if (main.chanceOf(80)) resAdd = resAdd.toUpperCase();
            result.append(resAdd);
        }

        return result.insert(4, "-").insert(8, "-").toString();
    }

    public static void createRedeem(String type, @Nullable ItemStack item, int amount, int maxRedeems, String code, String display) throws IOException {
        if (main.redeemCodes.get("Codes." + code) != null) return;

        main.redeemCodes.set("Codes." + code + ".RedeemDisplay", display);
        main.redeemCodes.set("Codes." + code + ".Type", type);
        main.redeemCodes.set("Codes." + code + ".Amount", amount);
        main.redeemCodes.set("Codes." + code + ".MaxRedeems", maxRedeems);
        main.redeemCodes.set("Codes." + code + ".RedeemedBy", new ArrayList<String>());

        String action = main.config.getString("RedeemTypes." + type + ".Action");

        if (action.equalsIgnoreCase("GiveItem")) {
            if (item == null) return;
            main.redeemCodes.set("Codes." + code + ".Item", item);
        }
        main.redeemCodes.save(main.redeemCodesFile);
    }

    public static RedeemState attemptRedeemCode(String code, Player player) {
        if (main.redeemCodes.get("Codes." + code) == null) return RedeemState.CODE_INVALID;

        String type = main.redeemCodes.getString("Codes." + code + ".Type");
        int amount = main.redeemCodes.getInt("Codes." + code + ".Amount");
        int maxRedeems = main.redeemCodes.getInt("Codes." + code + ".MaxRedeems");
        List<String> redeemedBy = main.redeemCodes.getStringList("Codes." + code + ".RedeemedBy");
        ItemStack item = main.redeemCodes.getItemStack("Codes." + code + ".Item");

        if (redeemedBy.size() >= maxRedeems) return RedeemState.CANT_BE_REDEEMED;

        if (redeemedBy.contains(player.getUniqueId().toString())) return RedeemState.ALREADY_REDEEMED;

        if (amount == 0) return RedeemState.ERROR;
        String action = main.config.getString("RedeemTypes." + type + ".Action");
        if (action.equalsIgnoreCase("GiveItem")) {
            if (item == null) return RedeemState.ERROR;
            if (player.getInventory().firstEmpty() == -1) return RedeemState.INVENTORY_FULL;
        }

        return RedeemState.SUCCESS;
    }

    public enum RedeemState {
        SUCCESS,
        CODE_INVALID,
        CANT_BE_REDEEMED,
        ALREADY_REDEEMED,
        INVENTORY_FULL,
        ERROR
    }
}
