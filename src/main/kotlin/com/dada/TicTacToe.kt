package com.dada

import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.content

object TicTacToe : KotlinPlugin() {
    private val gameList = mutableListOf<Game>()  //储存当前的房间的列表
    private const val ver = "0.1.1"
    private const val lastUpdatedTime = "9-4"
    private const val log = "使用了新的素材；不可跨群对战(正在添加跨群对战功能)"
    private const val gameMaster = 1461362731L
    private const val gameMasterXing = 729490703L
    private const val help = "反井字棋：对局双方轮流落子，谁先连成三子就输。"

    override fun onEnable() {
        super.onEnable()

        logger.info("Plugin loaded!")

        subscribeGroupMessages {
            case("反井字棋信息") {
                reply("版本：$ver\n作者：dada、星\n最后更新时间：$lastUpdatedTime\n更新日志：${log}\n还处于测试中")
                reply(help)
                reply("可用命令：\n#联机大厅\n#创建房间\n#退出房间\n#进入房间[房间号]\n#开始游戏\n下[行数][列数]")
            }
            /*
            改成了创建随机id的房间
            可能会摇出已有的房间从而覆盖掉,需要日后修复(或者改回随意命名得了)
            日后如果加入多种游戏,可以改成创建后询问该房间进行什么游戏
             */
            case("#创建房间") {
                if (playerExist(sender)) reply("你已经在其它房间内了")
                else {
                    val id = (1..999).random().toString()
                    val g = Game(id)
                    g.addMember(sender)
                    g.nowTurn = g.players[0]
                    gameList.add(g)
                    reply(At(sender) + "已创建房间$id")
                }
            }

            /*
            一个人不能进两个房间,要不然不知道他在下哪一个
             */
            startsWith("#进入房间") {
                try {
                    val id = message.content.substring(5)
                    if (playerExist(sender)) reply("你已经在某个房间中了")
                    else {
                        val g = getRoomById(id)
                        if (g.maxPlayerNumber == g.players.size) {
                            reply("此房间已满")
                        } else {
                            g.addMember(sender)
                            reply("已加入房间$id")
                        }
                    }
                } catch (e: Exception) {
                    reply("房间不存在哦")
                }
            }

            /*

             */
            case("#退出房间") {
                try {
                    val r = getRoomByPlayer(sender)
                    if (r.running) {
                        gameList.remove(r)
                        reply(At(sender) + "退出成功，系统自动判负，此房间关闭")
                    } else {
                        r.players.remove(sender)
                        if (r.players.size == 0) {
                            gameList.remove(r)
                            reply("退出成功，房间" + r.roomId + "因没有玩家而自动关闭")
                        } else {
                            reply(At(sender) + "退出成功")
                        }
                    }

                } catch (e: Exception) {
                    reply(At(sender) + "你都没加入")
                }
            }

            startsWith("下") {
                val location = message.content.substring(1).toList()
                val g = getRoomByPlayer(sender)
                if (g.running) {
                    if (g.nowTurn.id == sender.id) {
                        try {
                            g.layChess(location[0].toInt() - 49, location[1].toInt() - 49)
                            g.nextTurn()
                        } catch (e: Exception) {
                            reply(At(sender) + "你会不会下棋")
                        }
                    }
                }
            }

            case("#开始游戏") {
                val g = getRoomByPlayer(sender)
                if (g.players.size == 2) {
                    if (!g.running) {
                        g.beginning()
                        g.running = true
                    }
                } else reply("人数不足，当前房间只有一个人")
            }

            case("#联机大厅") {
                if (gameList.size == 0) {
                    reply("现在没有房间哦，可以试试输入#创建房间")
                } else {
                    var status = "现有的房间有："
                    var flagEmpty = true
                    gameList.forEach {
                        flagEmpty = false
                        status += "\n"
                        status += "[" + it.roomId + "]"
                        status += "已有" + it.players.size.toString() + "人"
                        status += "，房主为${it.players[0].nick}"
                    }
                    if (flagEmpty) {
                        status = "当前群内没有房间哦，可以试试输入#创建房间"
                    } else {
                        status += "\n输入#进入房间[房间号]即可加入哦~"
                    }
                    reply(status)
                }
            }

            sentBy(gameMaster) {
                if (message.content == "/结束") {
                    val id = message.content.substring(3)
                    gameList.remove(getRoomById(id))
                    reply("${id}已结束")
                }
            }
        }
    }

    private fun getRoomById(id: String): Game {
        gameList.forEach { if (it.roomId == id) return it }
        throw Exception("roomNotFound")
    }

    /*
    这里可能改得有点迷,我瞎搞的
     */
    private fun playerExist(player: Member): Boolean {
        gameList.forEach { it -> it.players.forEach { if (it == player) return true } }
        return false
    }

    private fun getRoomByPlayer(player: Member): Game {
        gameList.forEach {
            for (ply in it.players) {
                if (ply.id == player.id) return it
            }
        }
        throw Exception("roomNotFound")
    }
}