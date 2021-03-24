package man10newfarmplugin.man10newfarmplugin

import man10newfarmplugin.man10newfarmplugin.MNF.Companion.d
import man10newfarmplugin.man10newfarmplugin.MNF.Companion.da
import man10newfarmplugin.man10newfarmplugin.MNF.Companion.plugin
import man10newfarmplugin.man10newfarmplugin.MNF.Companion.prefix
import man10newfarmplugin.man10newfarmplugin.Util.changeint
import man10newfarmplugin.man10newfarmplugin.Util.givecrops
import man10newfarmplugin.man10newfarmplugin.Util.itemToBase64
import man10newfarmplugin.man10newfarmplugin.Util.parm
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

object FarmCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty()){
            
            return true
        }

        when(args[0]){
            "add"->{
                if (sender !is Player)return true
                if (!parm(sender))return true
                if (args.size != 4)return true
                val inv = sender.inventory
                plugin.config.set("farm.No${changeint(args[1]) ?:return true}.seed",inv.getItem(0))
                plugin.config.set("farm.No${changeint(args[1])}.crop",inv.getItem(1))
                plugin.config.set("farm.No${changeint(args[1])}.cropblock",inv.getItem(2)?.type?.name)
                plugin.config.set("farm.No${changeint(args[1])}.canb",inv.getItem(3)?.type?.name)
                plugin.config.set("farm.No${changeint(args[1])}.cropbreakitem",inv.getItem(4))
                plugin.config.set("farm.No${changeint(args[1])}.growc", changeint(args[2]))
                plugin.config.set("farm.No${changeint(args[1])}.ll", changeint(args[3]))
                plugin.saveConfig()
                sender.sendMessage(prefix + "configの更新が完了しました！")
                sender.sendMessage(prefix + "")
                return true
            }
            "addothercrops","addoc"->{
                if (sender !is Player)return true
                if (!parm(sender))return true
                if (args.size != 5)return true
                if (!plugin.config.isSet("farm.No${changeint(args[1]) ?:return true}"))return true
                val l = plugin.config.getStringList("farm.No${changeint(args[1])}.othercrops")
                l.add("${itemToBase64(sender.inventory.itemInMainHand)}:${args[2]}:${args[3]}:${args[4]}")
                plugin.config.set("farm.No${changeint(args[1])}.othercrops",l)
                plugin.saveConfig()
                sender.sendMessage(prefix + "othercropsの追加が完了しました")
                return true
            }
            "resetothercrops","resetoc"->{
                if (sender !is Player)return true
                if (!parm(sender))return true
                if (args.size != 2)return true
                val l = plugin.config.getStringList("farm.No${changeint(args[1]) ?:return true}.othercrops")
                l.clear()
                plugin.config.set("farm.No${changeint(args[1])}.othercrops",l)
                sender.sendMessage(prefix + "othercropsを削除しました")
                return true
            }
            "givecrops","give","gc"->{
                if (args.size != 2)return true
                if (sender !is Player)return true
                if (!parm(sender))return true
                changeint(args[1])?.let { givecrops(it,sender) }
            }
            "reload"-> {
                sender.sendMessage(prefix + "この処理には時間がかかる可能性があります")
                Thread{
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
                                Util.itemFromBase64(sp[0])?.let { li.add(it) }
                                list.add(IntRange(sp[1].toInt(), sp[2].toInt()))
                                listt.add(sp[3].toInt())
                            }
                            d.othercrops.add(li)
                            d.othercropschance.add(list)
                            d.othercropsdropchance.add(listt)
                        } else {
                            li.add(ItemStack(Material.AIR))
                            list.add(0..0)
                            listt.add(0)
                            d.othercrops.add(li)
                            d.othercropschance.add(list)
                            d.othercropsdropchance.add(listt)
                        }
                        c++
                        d.threadroop = plugin.config.getInt("farm.threadroop")

                    }
                    return@Thread
                }.start()
                sender.sendMessage(prefix + "読み込みが完了しました")
                return true
            }




        }

        return true
    }


}