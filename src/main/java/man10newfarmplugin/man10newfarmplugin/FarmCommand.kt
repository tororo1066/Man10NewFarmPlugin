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
import org.bukkit.block.Skull
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

object FarmCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty()){
            return true
        }

        when(args[0]){
            "help"->{
                if (sender !is Player)return true
                if (!parm(sender))return true
                sender.sendMessage("§a/mnf add (番号) (育つ確率1~100) (必要な光level)")
                sender.sendMessage("§aホットバーの左から順 種、出てくる作物、作物ブロック、植えられるブロック、作物を壊せるアイテム")
                sender.sendMessage("§a作物を追加します")
                sender.sendMessage("§b/mnf (addothercrops or addoc) (番号) (個数の最小値) (個数の最大値) (確率1~100)")
                sender.sendMessage("§b手に持っているアイテムを他の作物として追加します")
                sender.sendMessage("§d/mnf (resetothercrops or resetoc) (番号) othercropsを消します")
                sender.sendMessage("§e/mnf (givecrops、give、gc) (番号) 種を入手します")

            }

            "add"->{
                if (sender !is Player)return true
                if (!parm(sender))return true
                if (args.size != 4)return true
                val inv = sender.inventory
                plugin.config.set("farm.No${changeint(args[1]) ?:return true}.seed",inv.getItem(0))
                plugin.config.set("farm.No${changeint(args[1])}.crop",inv.getItem(1))
                plugin.config.set("farm.No${changeint(args[1])}.cropblock",inv.getItem(2)?.type?.name)
                if (inv.getItem(2)?.type == Material.PLAYER_HEAD){
                    val item = inv.getItem(2)?.itemMeta as SkullMeta
                    plugin.config.set("farm.No${changeint(args[1])}.crophead",item.owningPlayer?.uniqueId.toString())
                }
                plugin.config.set("farm.No${changeint(args[1])}.canb",inv.getItem(3)?.type?.name)
                plugin.config.set("farm.No${changeint(args[1])}.cropbreakitem",inv.getItem(4))
                plugin.config.set("farm.No${changeint(args[1])}.growc", changeint(args[2]))
                plugin.config.set("farm.No${changeint(args[1])}.ll", changeint(args[3]))
                plugin.saveConfig()
                sender.sendMessage(prefix + "configの更新が完了しました！")
                sender.sendMessage("$prefix/mnf reloadで適応してください")
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
                sender.sendMessage("$prefix/mnf reloadで適応してください")
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
                sender.sendMessage("$prefix/mnf reloadで適応してください")
                return true
            }
            "givecrops","give","gc"->{
                if (args.size != 2)return true
                if (sender !is Player)return true
                if (!parm(sender))return true
                changeint(args[1])?.let { givecrops(it,sender) }
            }




        }

        return true
    }


}