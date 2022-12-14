package de.base2code.clansystem.configuration;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class MessageConfig {
    private final File file;
    private YamlConfiguration config;

    private final Map<String, String> defaults = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public MessageConfig(File file) {
        this.file = file;
    }

    public void createIfNotExistsAndLoad() throws IOException {
        if (!file.exists()) {
            Files.createDirectories(file.toPath().getParent());
            Files.createFile(file.toPath());

            config = YamlConfiguration.loadConfiguration(file);
            setDefaults();
            save();
        } else {
            load();
            setDefaults();
            save();
        }
    }

    public void setDefaults() {
        defaults.put("prefix.clan", "&8[&bClan&8]");

        defaults.put("error.no-console", "%prefix% &cDu kannst diesen Command nicht von der Konsole verwenden!");
        defaults.put("error.no-permission", "%prefix% &cDu hast keine Berechtigung, diesen Command zu verwenden!");
        defaults.put("error.no-vault", "%prefix% &cDie Erstellung von Clans ist nicht möglich, da Vault nicht installiert ist und der Preis eines Clans nicht bei 0 liegt!");

        defaults.put("error.unknown-error", "%prefix% &cEin unbekannter Fehler ist aufgetreten!");
        defaults.put("error.clan-not-found", "%prefix% &cDer Clan wurde nicht gefunden!");

        defaults.put("error.clan-tag-is-taken", "%prefix% &cDieser Clan-Tag ist bereits vergeben!");
        defaults.put("error.clan-tag-is-invalid", "%prefix% &cDieser Clan-Tag ist ungültig!");
        defaults.put("error.clan-tag-is-too-long", "%prefix% &cDieser Clan-Tag ist zu lang!");
        defaults.put("error.clan-tag-is-too-short", "%prefix% &cDieser Clan-Tag ist zu kurz!");

        defaults.put("error.clan-name-is-too-long", "%prefix% &cDieser Clan-Name ist zu lang!");
        defaults.put("error.clan-name-is-too-short", "%prefix% &cDieser Clan-Name ist zu kurz!");
        defaults.put("error.clan-name-is-invalid", "%prefix% &cDieser Clan-Name ist ungültig!");
        defaults.put("error.clan-name-is-taken", "%prefix% &cDieser Clan-Name ist bereits vergeben!");

        defaults.put("error.already-in-clan", "%prefix% &cDu bist bereits in einem Clan!");

        defaults.put("error.not-enough-money", "%prefix% &cDu hast nicht genug Geld, um diesen Clan zu erstellen!");
        defaults.put("error.cant-leave-clan-because-owner", "%prefix% &cDu kannst deinen Clan nicht verlassen, da du der Besitzer bist!");
        defaults.put("error.not-in-clan", "%prefix% &cDu bist in keinem Clan!");
        defaults.put("commands.clan.leave.success", "%prefix% &7Du hast deinen Clan erfolgreich verlassen!");
        defaults.put("error.not-clan-owner", "%prefix% &cDu bist nicht der Besitzer des Clans!");
        defaults.put("commands.clan.delete.success", "%prefix% &7Du hast deinen Clan erfolgreich gelöscht!");

        defaults.put("error.unknown", "%prefix% &cEin unbekannter Fehler ist aufgetreten! Bitte kontaktiere einen Administrator!");

        defaults.put("commands.clan.confirmation.pending", "%prefix% &7Bitte bestätige diesen Command innerhalb von 5 min mit &a/clan confirm&7!");
        defaults.put("error.no-confirmation", "%prefix% &cDu hast keine Bestätigung ausstehend!");
        defaults.put("error.confirmation-timeout", "%prefix% &cDie Bestätigung ist abgelaufen!");

        defaults.put("error.player-not-found", "%prefix% &cDer Spieler wurde nicht gefunden!");
        defaults.put("error.player-not-in-clan", "%prefix% &cDer Spieler ist nicht in deinem Clan!");
        defaults.put("commands.clan.kick.success", "%prefix% &7Du hast den Spieler %player% erfolgreich aus deinem Clan gekickt!");

        defaults.put("commands.clan.create.success", "%prefix% &aDer Clan %clan_name% #%clan_tag% wurde erfolgreich erstellt!");
        defaults.put("commands.clan.info.info", "%prefix% &aClan-Info:\n" +
                " &7Clan-Tag: &b%clan_tag%\n" +
                " &7Clan-Name: &b%clan_name%\n" +
                " &7%clan_members%\n");

        defaults.put("commands.clan.syntax", "%prefix% &cSyntax: ");
        defaults.put("commands.clan.help.general",
                "\n%prefix% §bCommands:\n");

        defaults.put("commands.clan.help.create", "%prefix% &7/clan create <Name> <Tag>");
        defaults.put("commands.clan.help.info", "%prefix% §7Zeigt dir Informationen über einen Clan an");
        defaults.put("commands.clan.help.confirm", "%prefix% §7Bestätigt einen Command");
        defaults.put("commands.clan.help.delete", "%prefix% §7Löscht deinen Clan");
        defaults.put("commands.clan.help.kick", "%prefix% §7Kickt einen Spieler aus deinem Clan");
        defaults.put("commands.clan.help.leave", "%prefix% §7Verlässt deinen Clan");
        defaults.put("command.clan.help.promote", "%prefix% §7Befördert einen Spieler in deinem Clan");
        defaults.put("command.clan.help.demote", "%prefix% §7Degradiert einen Spieler in deinem Clan");
        defaults.put("commands.clan.help.invite", "%prefix% §7Lädt einen Spieler in deinen Clan ein");
        defaults.put("commands.clan.help.accept", "%prefix% §7Nimmt eine Clan-Einladung an");
        defaults.put("commands.clan.help.reject", "%prefix% §7Lehnt eine Clan-Einladung ab");

        defaults.put("error.cant-promote-owner", "%prefix% &cDu kannst den Besitzer nicht befördern!");
        defaults.put("commands.clan.promote.pending.owner", "%prefix% &cWARNUNG: Du bist gerade dabei %player% zum Besitzer zu befördern!");
        defaults.put("commands.clan.promote.pending.moderator", "%prefix% &cWARNUNG: Du bist gerade dabei %player% zum Moderator zu befördern!");
        defaults.put("commands.clan.demote.pending.moderator", "%prefix% &cWARNUNG: Du bist gerade dabei %player% zum normalen Clan-Mitglied zu degradieren!");
        defaults.put("error.cant-demote-owner", "%prefix% &cDu kannst den Besitzer nicht degradieren!");
        defaults.put("error.cant-demote-member", "%prefix% &cDu kannst ein Clan-Mitglied nicht degradieren! Um ein Clan Mitglied zu kicken, verwende bitte /clan kick <Spieler>!");
        defaults.put("commands.clan.demote.success", "%prefix% &7Du hast %player% erfolgreich degradiert!");
        defaults.put("commands.clan.promote.success", "%prefix% &7Du hast %player% erfolgreich befördert!");

        defaults.put("error.not-clan-permissions", "%prefix% &cDu hast keine Berechtigungen im Clan, diesen Command auszuführen!");

        defaults.put("commands.clan.info.owners", "&7Clan-Besitzer:");
        defaults.put("commands.clan.info.mods", "&7Clan-Moderatoren: ");
        defaults.put("commands.clan.info.members", "&7Clan-Mitglieder:");

        defaults.put("commands.clan.invite.success", "%prefix% &7Du hast %player% erfolgreich in deinen Clan eingeladen!");
        defaults.put("clan.invite.message", "%prefix% &7Du wurdest in den Clan %name%(#%tag%) eingeladen! &a/clan accept #%tag% &7oder &c/clan reject #%tag%");
        defaults.put("error.no-invite", "%prefix% &cDu hast keine Einladung für diesen Clan!");
        defaults.put("clan.accept.success", "%prefix% &aDu hast die Einladung erfolgreich angenommen!");
        defaults.put("clan.reject.success", "%prefix% &cDu hast die Einladung erfolgreich abgelehnt!");

        defaults.put("clan.accept.error.max-members", "%prefix% &cDer Clan hat bereits die maximale Anzahl an Mitgliedern!");

        for (String key : defaults.keySet()) {
            if (!config.contains(key)) {
                config.set(key, defaults.get(key));
            }
        }
    }

    public void load() throws IOException {
        config = YamlConfiguration.loadConfiguration(file);
    }

    public YamlConfiguration getConfiguration() {
        return config;
    }

    public void save() throws IOException {
        config.save(file);
    }

    public String getMessage(String path) {
        String data = config.getString(path);

        if (data == null || data.isEmpty()) {
            return "Message not found: " + path;
        }

        for (String key : config.getKeys(true)) {
            data = data.replace("!%" + key + "%", config.getString(key));
        }
        data = data.replace("%prefix%", config.getString("prefix.clan"));
        data = data.replace("&", "§");
        return data;
    }
}
