package man10newfarmplugin.man10newfarmplugin

import man10newfarmplugin.man10newfarmplugin.Util.itemFromBase64
import man10newfarmplugin.man10newfarmplugin.Util.itemToBase64
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Skull
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import kotlin.collections.ArrayList


class MNF : JavaPlugin() {


    companion object{
        const val prefix = "§e[§dMan10§6New§aFarm§e]§f "
        lateinit var plugin : MNF
        var farmdata = hashMapOf<Location, ItemStack>()
        val configdata = Configdata()
    }
    override fun onEnable() {

        plugin = this
        server.pluginManager.registerEvents(EventListener, this)
        getCommand("mnf")?.setExecutor(FarmCommand)
        getCommand("mnf")?.tabCompleter = this
        saveDefaultConfig()


        var c = 1
        while (plugin.config.isSet("farm.No$c")) {
            configdata.id.add(c)
            plugin.config.getItemStack("farm.No$c.seed")?.let { configdata.seed.add(it) }
            plugin.config.getItemStack("farm.No$c.crop")?.let { configdata.crop.add(it) }

            if (plugin.config.getString("farm.No$c.cropblock") == "PLAYER_HEAD") {
                configdata.cropblock.add(UUID.fromString(plugin.config.getString("farm.No$c.crophead")))
            } else {
                plugin.config.getString("farm.No$c.cropblock")?.let { Material.valueOf(it) }?.let { configdata.cropblock.add(it) }
            }

            plugin.config.getString("farm.No$c.canb")?.let { Material.valueOf(it) }?.let { configdata.canb.add(it) }
            configdata.growc.add(plugin.config.getInt("farm.No$c.growc"))
            configdata.ll.add(plugin.config.getInt("farm.No$c.ll"))
            plugin.config.getItemStack("farm.No$c.cropbreakitem")?.let { configdata.cropbreakitem.add(it) }
            val li = arrayListOf<ItemStack>()
            val list = arrayListOf<IntRange>()
            val listt = arrayListOf<Int>()
            if (plugin.config.isSet("farm.No$c.othercrops")) {
                for (i in plugin.config.getStringList("farm.No$c.othercrops")) {
                    val sp = i.split(":")
                    itemFromBase64(sp[0])?.let { li.add(it) }
                    list.add(IntRange(sp[1].toInt(), sp[2].toInt()))
                    listt.add(sp[3].toInt())
                }
                configdata.othercrops.add(li)
                configdata.othercropschance.add(list)
                configdata.othercropsdropchance.add(listt)
            }else{
                li.add(ItemStack(Material.AIR))
                list.add(0..0)
                listt.add(0)
                configdata.othercrops.add(li)
                configdata.othercropschance.add(list)
                configdata.othercropsdropchance.add(listt)
            }
            c++
            configdata.threadroop = config.getInt("farm.threadroop")

            val mysql = MySQLManager(plugin, "farmfirstload")
            val rs = mysql.query("SELECT * FROM crops_location;")
            while (rs?.next() == true) {
                farmdata[Location(Bukkit.getWorld(UUID.fromString(rs.getString("world"))), rs.getInt("x").toDouble(), rs.getInt("y").toDouble(), rs.getInt("z").toDouble())] = itemFromBase64(rs.getString("crops"))!!
            }
            rs?.close()
            mysql.close()

            object : BukkitRunnable() {
                override fun run() {
                    if (farmdata.size != 0) {
                        val mysql2 = MySQLManager(plugin, "farmload")
                        mysql2.execute("DELETE FROM crops_location;")
                        for (i in farmdata) {
                            val cropint = configdata.crop.indexOf(i.value)
                            if (i.key.block.type == Material.WHEAT){
                                mysql2.execute("INSERT INTO crops_location (x, y, z, world, crops) VALUES (${i.key.x}, ${i.key.y}, ${i.key.z}, '${i.key.world.uid}', '${itemToBase64(i.value)}');")
                                continue
                            }
                            if (configdata.cropblock[cropint] is UUID){
                                if (i.key.block.type != Material.PLAYER_HEAD)continue
                                val data = i.key.block.state as Skull
                                if (!data.hasOwner() && data.owningPlayer?.uniqueId != configdata.cropblock[cropint])continue
                                mysql2.execute("INSERT INTO crops_location (x, y, z, world, crops) VALUES (${i.key.x}, ${i.key.y}, ${i.key.z}, '${i.key.world.uid}', '${itemToBase64(i.value)}');")
                                continue
                            }else{
                                if (i.key.block.type != configdata.cropblock[cropint])continue
                                mysql2.execute("INSERT INTO crops_location (x, y, z, world, crops) VALUES (${i.key.x}, ${i.key.y}, ${i.key.z}, '${i.key.world.uid}', '${itemToBase64(i.value)}');")
                                continue
                            }

                        }
                        mysql2.close()
                    }
                }
            }.runTaskTimer(plugin, (configdata.threadroop * 20 * 60).toLong(), (configdata.threadroop * 20 * 60).toLong())


        }
    }

    override fun onDisable() {
        if (farmdata.size != 0) {
            val mysql2 = MySQLManager(plugin, "farmload")
            mysql2.execute("DELETE FROM crops_location;")
            for (i in farmdata) {
                val ii = configdata.crop.indexOf(i.value)
                if (i.key.block.type == Material.WHEAT){
                    mysql2.execute("INSERT INTO crops_location (x, y, z, world, crops) VALUES (${i.key.x}, ${i.key.y}, ${i.key.z}, '${i.key.world.uid}', '${itemToBase64(i.value)}');")
                    continue
                }
                if (configdata.cropblock[ii] is UUID){
                    if (i.key.block.type != Material.PLAYER_HEAD)continue
                    val data = i.key.block.state as Skull
                    if (!data.hasOwner() && data.owningPlayer?.uniqueId != configdata.cropblock[ii])continue
                    mysql2.execute("INSERT INTO crops_location (x, y, z, world, crops) VALUES (${i.key.x}, ${i.key.y}, ${i.key.z}, '${i.key.world.uid}', '${itemToBase64(i.value)}');")
                    continue
                }else{
                    if (i.key.block.type != configdata.cropblock[ii])continue
                    mysql2.execute("INSERT INTO crops_location (x, y, z, world, crops) VALUES (${i.key.x}, ${i.key.y}, ${i.key.z}, '${i.key.world.uid}', '${itemToBase64(i.value)}');")
                    continue
                }

            }
            mysql2.close()
        }
    }



}


class Configdata{
    var id = mutableListOf<Int>()
    var seed = mutableListOf<ItemStack>()
    var crop = mutableListOf<ItemStack>()
    var othercrops = mutableListOf<ArrayList<ItemStack>>()
    var othercropsdropchance = mutableListOf<ArrayList<Int>>()
    var othercropschance = mutableListOf<ArrayList<IntRange>>()
    var cropblock = mutableListOf<Any>()
    var canb = mutableListOf<Material>()
    var growc = mutableListOf<Int>()
    var ll = mutableListOf<Int>()
    var cropbreakitem = mutableListOf<ItemStack>()
    var threadroop : Int = 0
}