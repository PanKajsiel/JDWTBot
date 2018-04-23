package listeners;

import commands.essentials.Ping;
import commands.etc.BotStats;
import core.CoreCommands;
import core.Main;
import core.SSSS;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import utils.STATICS;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class BotListener extends ListenerAdapter{


    private void addToLogfile(MessageReceivedEvent e) throws IOException {

        File logFile = new File("CMDLOG.txt");
        BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, true));

        if (!logFile.exists())
            logFile.createNewFile();
        if (!e.getChannelType().equals(ChannelType.PRIVATE)) {
            bw.write(String.format("%s [%s (%s)] [%s (%s)] '%s'\n",
                    CoreCommands.getCurrentSystemTime(),
                    e.getGuild().getName(),
                    e.getGuild().getId(),
                    e.getAuthor().getName(),
                    e.getAuthor().getId(),
                    e.getMessage().getContentDisplay()));

        }
        else{
            bw.write(String.format("%s [%s (%s)] '%s'\n",
                    CoreCommands.getCurrentSystemTime(),
                    e.getAuthor().getName(),
                    e.getAuthor().getId(),
                    e.getMessage().getContentDisplay()));
        }
        bw.close();
    }


    @Override
    public void onMessageReceived(MessageReceivedEvent e) {

        BotStats.messagesProcessed++;

        //if (e.getChannelType().equals(ChannelType.PRIVATE)) return;

        if (e.getMessage().getContentDisplay().startsWith(SSSS.getPREFIX(e.getGuild())) && e.getMessage().getAuthor().getId() != e.getJDA().getSelfUser().getId()) {
            Ping.setInputTime(new Date().getTime());
            if (!commands.guildAdministration.Blacklist.check(e.getAuthor(), e.getGuild())) return;
            try {
                Main.handleCommand(Main.parser.parse(e.getMessage().getContentDisplay(), e));
                ArrayList<String> list = new ArrayList<>();
                if (STATICS.commandConsoleOutout) {
                    list.add(CoreCommands.getCurrentSystemTime());
                    if (e.getChannelType().equals(ChannelType.PRIVATE))
                        System.out.println(CoreCommands.getCurrentSystemTime() + " [Info] [Commands]: Command '" + e.getMessage().getContentDisplay() + "' was executed by '" + e.getAuthor() + ")!");
                    else{
                        System.out.println(CoreCommands.getCurrentSystemTime() + " [Info] [Commands]: Command '" + e.getMessage().getContentDisplay() + "' was executed by '" + e.getAuthor() + "' (" + e.getGuild().getName() + ")!");
                        list.add(e.getGuild().getId());
                        list.add(e.getMember().getEffectiveName());
                    }
                }
                list.add(e.getMessage().getContentDisplay());
                STATICS.cmdLog.add(list);
                addToLogfile(e);
            } catch (ParseException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

}

