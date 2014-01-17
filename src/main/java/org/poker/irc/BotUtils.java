package org.poker.irc;

import org.joda.money.BigMoney;

import java.text.NumberFormat;

public class BotUtils {
    public static void appendMoney(BigMoney bigMoney, StringBuilder sb) {
        sb.append(NumberFormat.getCurrencyInstance().format(bigMoney.getAmount()));
    }
}
