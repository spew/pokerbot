package org.poker.irc;

import org.joda.money.BigMoney;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class BotUtils {
  public static void appendMoney(BigMoney bigMoney, StringBuilder sb) {
      sb.append(NumberFormat.getCurrencyInstance().format(bigMoney.getAmount()));
  }

  private static String[] suffix = new String[]{"","k", "m", "b", "t"};
  private static int MAX_LENGTH = 4;

  public static String format(double number) {
    String r = new DecimalFormat("##0E0").format(number);
    r = r.replaceAll("E[0-9]", suffix[Character.getNumericValue(r.charAt(r.length() - 1)) / 3]);
    /*while(r.length() > MAX_LENGTH || r.matches("[0-9]+\\.[a-z]")){
      r = r.substring(0, r.length()-2) + r.substring(r.length() - 1);
    } */
    return r;
  }
}
