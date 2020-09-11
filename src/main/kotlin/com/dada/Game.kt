package com.dada

import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.uploadAsImage

/*
房间,或者说游戏的类
 */
class Game(n: String) {
    val players = mutableListOf<Member>() //对局者,应该叫player比较好点,现在改了(那我为什么不删掉这条注释emmm
    val roomId = n
    val maxPlayerNumber = 2
    lateinit var nowTurn: Member  //这储存了本回合的人
    private var chessBoard = ChessBoard()
    private var crossTurn = false  //判断当前是否为"X",先手执"X"
    var running = false
    private var playerInSameGroup: Boolean = true

    fun addMember(member: Member): Boolean {
        return if (players.size < 2) {
            players.add(member)
            false
        } else true
    }

    /*
    下棋.我不知道下棋的英语是啥,就乱编了一个
    不知道+1,首先排除chess
     */
    fun layChess(x: Int, y: Int) {
        if (chessBoard.chessBoard[y][x] == 0) {
            chessBoard.chessBoard[y][x] = if (crossTurn) 1 else 2
        } else {
            throw Exception("chessExists")
        }
    }

    /*
    不知道开局该叫啥
     */
    suspend fun beginning() {
        chessBoard = ChessBoard()
        crossTurn = false
        when ((1..8).random()) {
            1 -> {
                chessBoard.chessBoard[0][2] = 1
                chessBoard.chessBoard[1][1] = 2
            }
            2 -> {
                chessBoard.chessBoard[2][0] = 1
                chessBoard.chessBoard[1][1] = 2
            }
            3 -> {
                chessBoard.chessBoard[0][1] = 1
                chessBoard.chessBoard[1][2] = 2
            }
            4 -> {
                chessBoard.chessBoard[2][3] = 1
                chessBoard.chessBoard[1][2] = 2
            }
            5 -> {
                chessBoard.chessBoard[1][0] = 1
                chessBoard.chessBoard[2][1] = 2
            }
            6 -> {
                chessBoard.chessBoard[3][2] = 1
                chessBoard.chessBoard[2][1] = 2
            }
            7 -> {
                chessBoard.chessBoard[1][3] = 1
                chessBoard.chessBoard[2][2] = 2
            }
            8 -> {
                chessBoard.chessBoard[3][1] = 1
                chessBoard.chessBoard[2][2] = 2
            }
        }
        if (players[0].group.id != players[1].group.id) playerInSameGroup = false
        nextTurn()
    }

    suspend fun nextTurn() {
        /*
        每次前进一个回合, 都判断一次胜负.
         */
        if (playerInSameGroup) {
            when (judge()) {
                0 -> {
                    nowTurn.group.sendMessage(chessBoard.board().uploadAsImage(nowTurn.group))
                    nowTurn.group.sendMessage(At(nowTurn) + "你输了哦" + chessBoard.board().uploadAsImage(nowTurn.group))
                    this.running = false
                    this.chessBoard = ChessBoard()
                }
                1 -> {
                    nowTurn = if (nowTurn == players[0]) {
                        players[1]
                    } else {
                        players[0]
                    }
                    crossTurn = !crossTurn
                    nowTurn.group.sendMessage(
                        At(nowTurn) + "轮到你落子了哦（你是\"" + (if (crossTurn) "Ｘ" else "Ｏ") + "\"）" + chessBoard.board()
                            .uploadAsImage(nowTurn.group)
                    )

                }
                2 -> {
                    nowTurn.group.sendMessage("平局了哦")
                    this.running = false
                    this.chessBoard = ChessBoard()
                }
            }
        }else{
            /*
            正在修改中，还不能使用。
             */
            when (judge()) {
                0 -> {
                    nowTurn.group.sendMessage(chessBoard.board().uploadAsImage(nowTurn.group))
                    nowTurn.group.sendMessage(At(nowTurn) + "你输了哦" + chessBoard.board().uploadAsImage(nowTurn.group))
                    this.running = false
                    this.chessBoard = ChessBoard()
                }
                1 -> {
                    nowTurn = if (nowTurn == players[0]) {
                        players[1]
                    } else {
                        players[0]
                    }
                    crossTurn = !crossTurn
                    nowTurn.group.sendMessage(
                        At(nowTurn) + "轮到你落子了哦（你是\"" + (if (crossTurn) "Ｘ" else "Ｏ") + "\"）" + chessBoard.board()
                            .uploadAsImage(nowTurn.group)
                    )

                }
                2 -> {
                    nowTurn.group.sendMessage("平局了哦")
                    this.running = false
                    this.chessBoard = ChessBoard()
                }
            }
        }
    }

    private fun theOtherPlayer(player: Member): Member {
        return if (players[0] == player) players[1] else players[0]
    }

    private fun judge(): Int {
        return chessBoard.judge(if (crossTurn) 1 else 2)
    }
}

