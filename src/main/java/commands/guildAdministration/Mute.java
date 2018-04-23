package commands.guildAdministration;

import commands.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import utils.STATICS;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.*;
import java.util.List;

/**
 * Created by zekro on 14.05.2017 / 10:34
 * DiscordBot/commands.etc
 * © zekro 2017
 *
 * THIS COMMAND IS CREATED BY CONTRIBUTOR naix (https://github.com/NaixTimo)
 */


public class Mute implements Command {

	//DISCLAIMER: ICH WEIß NICHT OB ES FUNKTIONIERT x)

    private static void Mute(MessageReceivedEvent event, Member member, String reason, String mutedBy) throws IOException {

        Properties properties = new Properties();
        OutputStream outputStream;
        Guild g = event.getGuild();

        outputStream = new FileOutputStream("MUTES/" + member.getUser().getId() + "/mute.Settings");
        properties.setProperty("reason", reason);
        properties.setProperty("muted_by", mutedBy);
        properties.store(outputStream, null);

        ArrayList<Role> mutedRole = new ArrayList<>();
        mutedRole.add(event.getGuild().getRolesByName("Muted", true).get(0));

        g.getController().addRolesToMember(member, mutedRole);

    }
    private static void Unmute(MessageReceivedEvent event, Member member) throws IOException{

        Guild g = event.getGuild();
        File file = new File("MUTES/" + member.getUser().getId() + "/mute.Settings");
        ArrayList<Role> mutedRole = new ArrayList<>();
        mutedRole.add(event.getGuild().getRolesByName("Muted", true).get(0));

        if (file.exists()) file.delete();
        g.getController().removeRolesFromMember(member, mutedRole);

    }
    private static boolean isMuted(Member member) {
        File f = new File("MUTES/" + member.getUser().getId() + "/mute.Settings");
        return f.exists();
    }
    private static boolean isValidID(MessageReceivedEvent event, String uID) {

        Guild g = event.getGuild();

        List<String> IDs = new ArrayList<>();

        for ( Member member : g.getMembers() ) {
            IDs.add(member.getUser().getId());
        }

        return IDs.contains(uID);

    }
    private static boolean isMutable(MessageReceivedEvent event, Member member) {
        Guild g = event.getGuild();
        return !member.getRoles().contains(event.getGuild().getRolesByName("Muted", true).get(0));
    }
    private static void sendMsg(MessageReceivedEvent e, String message, String type, int seconds) {
        EmbedBuilder err = new EmbedBuilder().setColor(Color.WHITE);
        EmbedBuilder ok = new EmbedBuilder().setColor(e.getMember().getColor());
        if (type.equalsIgnoreCase("ok")) {
            Message msg = e.getChannel().sendMessage(
                    ok.setDescription(message).build()
            ).complete();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    msg.delete().queue();
                }
            }, seconds * 1000);
        }
        if (type.equalsIgnoreCase("err")) {
            Message msg = e.getChannel().sendMessage(
                    err.setDescription(message).build()
            ).complete();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    msg.delete().queue();
                }
            }, seconds * 1000);
        }
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) { return false; }

    @Override
    public void action(String[] args, MessageReceivedEvent event) throws ParseException, IOException {

        if (core.Perms.check(1, event)) return;

        if (args[1].isEmpty()) args[1] = "Unknown";

        if (!args[0].isEmpty() && isValidID(event, args[0])) {
            Member member = event.getGuild().getMemberById(args[0]);
            if (isMutable(event, event.getGuild().getMemberById(args[0]))) {
                if (isMuted(member)) Unmute(event, member);
                sendMsg(event, "Unmuted " + event.getGuild().getMemberById(args[0]).getEffectiveName() + "(" + args[0] + ")", "ok", 10);
                if (!isMuted(member)) Mute(event, member, args[1], event.getAuthor().getName());
                sendMsg(event, "Muted " + event.getGuild().getMemberById(args[0]).getEffectiveName() + "(" + args[0] + ")", "ok", 10);
            } else {
                sendMsg(event,"User is not Mutable!", "err", 10);
            }
        } else {
            sendMsg(event, "Please enter a valid UserID!", "err", 10);
        }

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
         return "Использование:\n" +                                                    // EDIT BY zekro
                "**mute <userID> <причина>**  -  `Mute/Unmute a Member`";        //
    }

    @Override
    public String description() {
        return "Отключение голоса пользователя";
    }

    @Override
    public String commandType() {
        return STATICS.CMDTYPE.guildadmin;
    }

    @Override
    public int permission() {
        return 2;
    }
}
