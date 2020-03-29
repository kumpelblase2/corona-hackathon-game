package de.eternalwings.bukkit.zombiecorona

import org.bukkit.Material
import org.bukkit.entity.Player
import kotlin.math.min

private val Player.isInWater: Boolean
    get() {
        return this.location.add(0.0, 0.5, 0.0).block.type == Material.WATER
    }

sealed class GameState {
    abstract fun tick(game: Game): GameState?

    protected fun updateInfectionOf(player: Player, game: Game) {
        if (player.isInWater) {
            game.infectionStates.merge(player.uniqueId, -5.0 / 20) { old, adjusted ->
                (old + adjusted).coerceAtLeast(0.0)
            }
        } else {
            game.infectionStates.merge(
                player.uniqueId,
                game.currentInfectionIncrease / 20
            ) { old, adjusted -> old + adjusted }
        }

        game.bossBars[player.uniqueId]!!.progress = min(game.infectionStates[player.uniqueId]!! / 100, 1.0)
        if (game.infectionStates[player.uniqueId]!! >= 100) {
            game.infectPlayer(player)
        }
    }

    object NotStarted : GameState() {
        override fun tick(game: Game): GameState? {
            game.players.forEach { player ->
                player.sendMessage("Die Zombies kommen und bringen das Virus mit sich!")
                player.sendMessage("Seht zu, dass ihr euch immer schön wäscht, bevor ihr infiziert werdet!")
                player.sendMessage("Bereitet euch vor; die ersten Zombies sind gleich unterwegs!")
            }
            return CooldownState(120, false)
        }
    }

    class CooldownState(private var cooldownTicks: Int = 60, private val tickInfection: Boolean = true) : GameState() {

        override fun tick(game: Game): GameState? {
            cooldownTicks -= 1
            if (tickInfection) {
                game.activePlayers.forEach { player ->
                    updateInfectionOf(player, game)
                }
            }

            if (cooldownTicks <= 0) {
                return WaveState()
            }
            return null
        }

    }

    class WaveState : GameState() {
        private var spawned = false

        override fun tick(game: Game): GameState? {
            if (!spawned) {
                spawned = true
                game.nextWave()
            } else {
                increasePlayerSickness(game)
            }

            if (!game.hasZombiesRemaining) {
                return CooldownState(game.cooldownTicks)
            }
            return null
        }

        private fun increasePlayerSickness(game: Game) {
            game.activePlayers.forEach { player ->
                this.updateInfectionOf(player, game)
            }
        }

    }
}
