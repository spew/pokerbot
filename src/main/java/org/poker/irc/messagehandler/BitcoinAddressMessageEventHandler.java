package org.poker.irc.messagehandler;

import org.pircbotx.hooks.events.MessageEvent;
import org.poker.irc.MessageEventHandler;
import org.poker.irc.blockchain.BlockChainInfoClient;
import org.poker.irc.blockchain.SingleAddress;

public class BitcoinAddressMessageEventHandler implements MessageEventHandler {
  private BlockChainInfoClient blockChainInfoClient = new BlockChainInfoClient();

  @Override
  public String getMessageRegex() {
    return "^[13][a-zA-Z0-9]{26,33}";
  }

  @Override
  public String[] getMessagePrefixes() {
    return new String[0];
  }

  @Override
  public void onMessage(MessageEvent event) {
    String message = event.getMessage();
    SingleAddress singleAddress = blockChainInfoClient.getSingleAddress(message);
    event.getChannel().send().message(blockChainInfoClient.getSingleAddressUrl(message));
    String channelMessage = String.format("total: %1$,.8f BTC | received: %2$,.8f BTC | sent: %3$,.8f BTC",
        singleAddress.getFinalBalanceBtc(),
        singleAddress.getReceivedCountBtc(),
        singleAddress.getSentCountBtc());
    event.getChannel().send().message(channelMessage);
  }

  @Override
  public String getDescription() {
    return null;
  }
}
