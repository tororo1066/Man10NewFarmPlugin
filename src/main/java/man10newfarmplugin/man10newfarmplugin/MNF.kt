package man10newfarmplugin.man10newfarmplugin

import man10newfarmplugin.man10newfarmplugin.Util.itemFromBase64
import man10newfarmplugin.man10newfarmplugin.Util.itemToBase64
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.StringUtil
import java.util.*
import kotlin.collections.ArrayList


class MNF : JavaPlugin() {

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        val com = ArrayList<String>()
        val compl = ArrayList<String>()
        if (args.size == 1){
            if (sender.hasPermission("admin")){
                com.add("add")
                StringUtil.copyPartialMatches(args[0], com,compl)
            }
        }
        if (args.size == 2){
            if (sender.hasPermission("admin")){
                if (args[0] == "add"){

                }
                if (args[0] == "addothercrops" || args[0] == "addoc"){

                }
                StringUtil.copyPartialMatches(args[1], com,compl)
            }
        }

        com.sort()
        return com
    }

    companion object{
        const val prefix = ""
        lateinit var plugin : MNF
        var da = hashMapOf<Location, ItemStack>()
        val d = Configdata()
    }
    override fun onEnable() {

        plugin = this
        server.pluginManager.registerEvents(EventListener, this)
        getCommand("mnf")?.setExecutor(FarmCommand)
        getCommand("mnf")?.tabCompleter = this
        saveDefaultConfig()


        var c = 1
        while (plugin.config.isSet("farm.No$c")) {
            d.id.add(c)
            plugin.config.getItemStack("farm.No$c.seed")?.let { d.seed.add(it) }
            plugin.config.getItemStack("farm.No$c.crop")?.let { d.crop.add(it) }

            if (plugin.config.getString("farm.No$c.cropblock") == "PLAYER_HEAD") {
                d.cropblock.add(UUID.fromString(plugin.config.getString("farm.No$c.crophead")))
            } else {
                plugin.config.getString("farm.No$c.cropblock")?.let { Material.valueOf(it) }?.let { d.cropblock.add(it) }
            }

            plugin.config.getString("farm.No$c.canb")?.let { Material.valueOf(it) }?.let { d.canb.add(it) }
            d.growc.add(plugin.config.getInt("farm.No$c.growc"))
            d.ll.add(plugin.config.getInt("farm.No$c.ll"))
            plugin.config.getItemStack("farm.No$c.cropbreakitem")?.let { d.cropbreakitem.add(it) }
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
                d.othercrops.add(li)
                d.othercropschance.add(list)
                d.othercropsdropchance.add(listt)
            }else{
                li.add(ItemStack(Material.AIR))
                list.add(0..0)
                listt.add(0)
                d.othercrops.add(li)
                d.othercropschance.add(list)
                d.othercropsdropchance.add(listt)
            }
            c++

            val mysql = MySQLManager(plugin, "farmfirstload")
            val rs = mysql.query("SELECT * FROM crops_location;")
            while (rs?.next() == true) {
                da[Location(Bukkit.getWorld(UUID.fromString(rs.getString("world"))), rs.getInt("x").toDouble(), rs.getInt("y").toDouble(), rs.getInt("z").toDouble())] = itemFromBase64(rs.getString("crops"))!!
            }
            rs?.close()
            mysql.close()
            object : BukkitRunnable() {
                override fun run() {
                    mysql.execute("DELETE FROM crops_location;")
                    for (i in da) {
                        mysql.execute("INSERT INTO crops_location (x, y, z, world, crops) VALUES (${i.key.x}, ${i.key.y}, ${i.key.z}, '${i.key.world.uid}', '${itemToBase64(i.value)}');")
                    }
                }
            }.runTaskTimer(plugin, (d.threadroop * 20 * 60).toLong(), (d.threadroop * 20 * 60).toLong())


        }
    }

    override fun onDisable() {
            val mysql = MySQLManager(plugin, "farmload")
            mysql.execute("DELETE FROM crops_location;")
            for (i in da){
                mysql.execute("INSERT INTO crops_location (x, y, z, world, crops) VALUES (${i.key.x}, ${i.key.y}, ${i.key.z}, '${i.key.world.uid}', '${itemToBase64(i.value)}');")
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