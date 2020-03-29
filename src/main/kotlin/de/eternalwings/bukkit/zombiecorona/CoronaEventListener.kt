package de.eternalwings.bukkit.zombiecorona

import org.bukkit.GameMode
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Zombie
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityCombustEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class CoronaEventListener(private val plugin: CoronaVirusPlugin) : Listener {

    @EventHandler
    fun onPlayerJoin(playerJoinEvent: PlayerJoinEvent) {
        val player = playerJoinEvent.player
        player.sendMessage("Um beim Spiel teilzunehmen, muss du erst in eine Lobby gelangen.")
        player.sendMessage("Dazu einfach \"/join\" schreiben.")
        player.sendMessage("Wenn alle Spieler, die mitspielen wollen, das getan haben,")
        player.sendMessage("Kann einer das Spielt mit \"/start\" starten!")
    }

    @EventHandler
    fun onZombieDeath(entityDeathEvent: EntityDeathEvent) {
        if(entityDeathEvent.entity.isZombieOfGame()) {
            plugin.gameManager.currentGame?.killZombie(entityDeathEvent.entity as Zombie)
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onZombieCombust(entityCombustEvent: EntityCombustEvent) {
        if(entityCombustEvent.entity.isZombieOfGame()) {
            entityCombustEvent.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerLeave(playerLeaveEvent: PlayerQuitEvent) {
        val currentGame = plugin.gameManager.currentGame ?: return
        if (currentGame.players.contains(playerLeaveEvent.player)) {
            if (currentGame.players.size == 1) {
                plugin.gameManager.finishGame()
            } else {
                currentGame.removePlayer(playerLeaveEvent.player)
            }
        }
    }

    @EventHandler
    fun onPlayerDeath(playerDeathEvent: PlayerDeathEvent) {
        val currentGame = plugin.gameManager.currentGame ?: return
        if(currentGame.players.contains(playerDeathEvent.entity)) {
            if(currentGame.players.size == 1) {
                plugin.gameManager.finishGame()
            } else {
                currentGame.removePlayer(playerDeathEvent.entity)
                playerDeathEvent.entity.gameMode = GameMode.SPECTATOR
                playerDeathEvent.drops.clear()
                playerDeathEvent.entity.teleport(playerDeathEvent.entity.location)
            }
        }
    }

    @EventHandler
    fun onPlayerDamage(damageEvent: EntityDamageByEntityEvent) {
        val currentGame = plugin.gameManager.currentGame ?: return
        val damagee = damageEvent.entity
        if(damagee !is Player) return

        if(currentGame.players.contains(damagee)) {
            currentGame.increaseInfection(damagee)
        }
    }

    private fun Entity.isZombieOfGame() : Boolean {
        return this is Zombie && this.hasMetadata(Game.ZOMBIE_PARTICIPANT_METADATA_KEY)
    }
}
