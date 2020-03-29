package de.eternalwings.bukkit.zombiecorona

import org.bukkit.entity.Player

class GameManager(private val configuration: Configuration, private val plugin: CoronaVirusPlugin) {

    var currentGame: Game? = null
        private set
    private var inLobby: Set<Player> = emptySet()

    fun startGame() {
        currentGame = Game(inLobby.toList(), plugin, configuration)
        inLobby = emptySet()
    }

    fun tickGame() {
        currentGame?.tick()
    }

    fun addToLobby(player: Player) {
        inLobby = inLobby + player
    }

    fun finishGame() {
        currentGame?.end()
        currentGame = null
    }

}
