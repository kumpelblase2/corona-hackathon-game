package de.eternalwings.bukkit.zombiecorona

import org.bukkit.plugin.java.JavaPlugin

class CoronaVirusPlugin : JavaPlugin() {

    var configuration: Configuration = Configuration()
    lateinit var gameManager: GameManager

    override fun onEnable() {
        val zombieIncrease = config.getDouble("zombie-increase", 1.1)
        val infectionIncrease = config.getDouble("infection-increase", 1.1)
        val zombieCount = config.getInt("zombie-count", 3)
        val cooldown = config.getInt("cooldown", 5)
        configuration = Configuration(zombieIncrease, infectionIncrease, zombieCount, cooldown)

        getCommand("start")!!.setExecutor(StartCommand(this))
        getCommand("join")!!.setExecutor(JoinCommand(this))
        server.pluginManager.registerEvents(CoronaEventListener(this), this)
        gameManager = GameManager(configuration, this)

        server.scheduler.scheduleSyncRepeatingTask(this, gameManager::tickGame, 1, 1)
    }
}
