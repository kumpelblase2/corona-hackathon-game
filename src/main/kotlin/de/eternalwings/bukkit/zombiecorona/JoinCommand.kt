package de.eternalwings.bukkit.zombiecorona

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class JoinCommand(private val plugin: CoronaVirusPlugin) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(sender !is Player) {
            sender.sendMessage("Cannot join as console :(")
            return true
        }
        plugin.gameManager.addToLobby(sender)
        sender.sendMessage("Dem nächsten Spiel beigetreten. Ihr solltet euch schon einmal vorbereiten...")
        return false
    }
}
