package commands.chat;

import commands.Command;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import utils.STATICS;

public class Say implements Command {
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {

        if (core.Perms.check(1, event)) return;

        String serverID = event.getGuild().getId();
        String channel = event.getTextChannel().getName();

        event.getMessage().delete().queue();

        String output = "";
        if (args.length > 0) {
            for (String e : args) {
                output += e + " ";
            }
        }

        event.getTextChannel().sendMessage(":loudspeaker:   " + output).queue();
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return "Использование: -say <сообщение>";
    }

    @Override
    public String description() {
        return "Написать сообщение в чат от имени бота";
    }

    @Override
    public String commandType() {
        return STATICS.CMDTYPE.chatutils;
    }

    @Override
    public int permission() {
        return 2;
    }
}
