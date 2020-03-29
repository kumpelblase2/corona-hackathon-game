package de.eternalwings.bukkit.zombiecorona

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class StartCommand(private val plugin: CoronaVirusPlugin) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        plugin.gameManager.startGame()
        return false
    }
}
