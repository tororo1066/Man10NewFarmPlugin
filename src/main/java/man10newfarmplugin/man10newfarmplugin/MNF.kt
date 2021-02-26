package man10newfarmplugin.man10newfarmplugin

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import java.util.*

class MNF : JavaPlugin() {

    companion object{
        const val prefix = ""
        lateinit var plugin : MNF
        var da = hashMapOf<Location,String>()
    }
    override fun onEnable() {

        plugin = this
        server.pluginManager.registerEvents(EventListener,this)
        getCommand("mnf")?.setExecutor(FarmCommand)
        saveDefaultConfig()

        Thread{
            val mysql = MySQLManager(plugin, "farmfirstload")
            val rs = mysql.query("SELECT * FROM crops_location;")
            while (rs?.next() == true){
                da[Location(Bukkit.getWorld(UUID.fromString(rs.getString("world"))),rs.getInt("x").toDouble(),rs.getInt("y").toDouble(),rs.getInt("z").toDouble())] = rs.getString("crops")
            }
            rs?.close()
            mysql.close()
            return@Thread
        }.start()
        object : BukkitRunnable(){
            override fun run() {

                val mysql = MySQLManager(plugin, "farmload")
                mysql.execute("DELETE FROM crops_location;")
                for (i in da){
                    mysql.execute("INSERT INTO crops_location (x, y, z, world, crops) VALUES (${i.key.x}, ${i.key.y}, ${i.key.z}, '${i.key.world.uid}', '${i.value}');")
                }

            }
        }.runTaskTimer(plugin,plugin.config.getLong("farm.threadroop") * 20 * 60,plugin.config.getLong("farm.threadroop") * 20 * 60)
    }

    override fun onDisable() {
            val mysql = MySQLManager(plugin, "farmload")
            mysql.execute("DELETE FROM crops_location;")
            for (i in da){
                mysql.execute("INSERT INTO crops_location (x, y, z, world, crops) VALUES (${i.key.x}, ${i.key.y}, ${i.key.z}, '${i.key.world.uid}', '${i.value}');")
            }

    }



}

