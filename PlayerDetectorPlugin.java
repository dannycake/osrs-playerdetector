package net.runelite.client.plugins.playerdetector;

import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.client.Notifier;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@PluginDescriptor(
        name = "Player Detector",
        description = "Detects new players in your vicinity",
        tags = {"players", "detector"}
)
public class PlayerDetectorPlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    private ChatMessageManager chatMessageManager;

    @Inject
    private Notifier notifier;

    private final Set<String> seenPlayers = new HashSet<>();

    @Override
    protected void startUp() throws Exception
    {
        super.startUp();
        seenPlayers.clear();
    }

    @Override
    protected void shutDown() throws Exception
    {
        super.shutDown();
        seenPlayers.clear();
    }

    @Subscribe
    public void onGameTick(GameTick gameTick)
    {
        List<Player> players = client.getPlayers();

        if (players.isEmpty())
        {
            return;
        }

        for (Player player : players)
        {
            String playerName = player.getName();

            if (!seenPlayers.contains(playerName))
            {
                seenPlayers.add(playerName);
                String message = "New player detected: " + playerName;
                sendMessageToChat(message);
                notifier.notify(message);
            }
        }
    }

    private void sendMessageToChat(String message)
    {
        String formattedMessage = new ChatMessageBuilder()
                .append(ChatColorType.HIGHLIGHT)
                .append(message)
                .build();

        chatMessageManager.queue(QueuedMessage.builder()
                .type(ChatMessageType.GAMEMESSAGE)
                .runeLiteFormattedMessage(formattedMessage)
                .build());
    }
}
