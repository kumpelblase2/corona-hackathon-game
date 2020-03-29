package de.eternalwings.bukkit.zombiecorona

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import org.bukkit.entity.Zombie
import org.bukkit.metadata.FixedMetadataValue
import java.lang.Math.pow
import java.util.*
import kotlin.math.abs
import kotlin.math.pow
import kotlin.random.Random

class Game(var players: List<Player>, private val plugin: CoronaVirusPlugin, private val configuration: Configuration) {

    var currentWave: Int = 0
    var remainingZombies: List<Zombie> = emptyList()
        private set
    val infectionStates: MutableMap<UUID, Double> = mutableMapOf()
    val bossBars: MutableMap<UUID,BossBar> = mutableMapOf()
    val currentZombieIncrease: Double
        get() = configuration.zombieIncrease.pow(currentWave)

    val currentInfectionIncrease: Double
        get() = configuration.infectionIncrease.pow(currentWave)

    val hasZombiesRemaining: Boolean
        get() = remainingZombies.isNotEmpty()

    val activePlayers: List<Player>
        get() = players.filter { it.gameMode == GameMode.SURVIVAL }

    val cooldownTicks = configuration.cooldownTime * 20

    private val zombieSpawnCount = configuration.zombieCount * players.size

    private var state: GameState = GameState.NotStarted

    init {
        players.forEach { player ->
            infectionStates[player.uniqueId] = 0.0
            bossBars[player.uniqueId] = Bukkit.getServer().createBossBar("Infektion", BarColor.GREEN, BarStyle.SOLID).also {
                it.progress = 0.0
                it.isVisible = true
                it.addPlayer(player)
            }
        }
    }

    fun nextWave() {
        currentWave += 1
        val nextZombieCount = zombieSpawnCount * currentZombieIncrease
        remainingZombies = (0..nextZombieCount.toInt()).map { spawnZombie() }
        players.forEach { it.sendMessage("Wave $currentWave - Start! ${nextZombieCount.toInt()} Zombies") }
    }

    fun killZombie(zombie: Zombie) {
        remainingZombies = remainingZombies - zombie
        players.forEach { player ->
            player.sendMessage(ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Nur noch ${remainingZombies.size} übrig")
        }
    }

    fun tick() {
        val nextState = state.tick(this)
        if(nextState != null) {
            state = nextState
        }
    }

    private fun spawnZombie(spawnLocation: Location = randomLocation()): Zombie {
        val world = spawnLocation.world!!
        val zombie = world.spawn(spawnLocation, Zombie::class.java)
        zombie.setMetadata(ZOMBIE_PARTICIPANT_METADATA_KEY, FixedMetadataValue(plugin, true))
        zombie.target = players.closestTo(spawnLocation)
        println("Spawning zombie at $spawnLocation")
        return zombie
    }

    private fun randomLocation(): Location {
        val randomPlayer = players.random()
        val playerLocation = randomPlayer.location
        for(i in 0 until 10) {
            val randomOffsetX = getRandomOffset()
            val randomOffsetZ = getRandomOffset()
            val highest = randomPlayer.world.getHighestBlockYAt(playerLocation.blockX + randomOffsetX, playerLocation.blockZ + randomOffsetZ) + 1
            val heightOffset = highest - playerLocation.blockY
            if(abs(heightOffset) < 20) {
                return playerLocation.add(randomOffsetX.toDouble(), heightOffset.toDouble(), randomOffsetZ.toDouble())
            }
        }

        return playerLocation // This should usually never happen, but just in case
    }

    private fun getRandomOffset() = (4..10).random() * (if (Random.nextBoolean()) -1 else 1)

    private fun List<Player>.closestTo(location: Location): Player {
        return this.minBy { it.location.distanceSquared(location) }!!
    }

    fun removePlayer(entity: Player) {
        infectionStates.remove(entity.uniqueId)
        val removedBar = bossBars.remove(entity.uniqueId)!!
        removedBar.removeAll()
    }

    fun end() {
        players.forEach { it.sendMessage("Der Virus hat gesiegt. Nächstes mal mehr Hände waschen!") }
        players.forEach { it.gameMode = GameMode.SURVIVAL }
        players.iterator().forEach { removePlayer(it) }
        remainingZombies.iterator().forEach { it.remove() }
        remainingZombies = emptyList()
    }

    fun infectPlayer(player: Player) {
        players.forEach { it.sendMessage("${player.displayName} hat zu lange nicht Hände gewaschen und wurde infiziert!") }
        removePlayer(player)
        for(i in 1..3) {
            spawnZombie(player.location)
        }

        player.gameMode = GameMode.SPECTATOR
        if(activePlayers.isEmpty()) {
           this.end()
        }
    }

    fun increaseInfection(damagee: Player) {
        infectionStates.merge(damagee.uniqueId, 5.0) { old, added -> old + added }
    }

    companion object {
         const val ZOMBIE_PARTICIPANT_METADATA_KEY = "CORONA"
     }
}
