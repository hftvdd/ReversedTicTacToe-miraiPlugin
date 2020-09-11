package com.dada

import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import javax.imageio.ImageIO

class ChessBoard {
    /*
    4*4的intArray,储存棋盘.
     */
    val chessBoard = Array(4) { IntArray(4) { 0 } }
    /*
    private val black = ImageIO.read(File("data\\tictactoe\\1.png"))
    private val white = ImageIO.read(File("data\\tictactoe\\2.png"))
    private val blankBoard = ImageIO.read(File("data\\tictactoe\\0.png"))
     */
    private val black = ImageIO.read(javaClass.getResourceAsStream("/tictactoe/1.png"))
    private val white = ImageIO.read(javaClass.getResourceAsStream("/tictactoe/2.png"))
    private val blankBoard = ImageIO.read(javaClass.getResourceAsStream("/tictactoe/0.png"))

    /*
    这里胜负判段不完整,缺少了一些可能输的情况.有空帮忙改改
    “断”打错了↑ (判断逻辑改好了)
     */
    fun judge(chess: Int): Int {
        /*
        0:一方出局
        1:正常继续
        2:下满平局
         */
        for (i in (0..3)) {
            for (j in (0..1)) {
                if (chessBoard[i][j] == chess && chessBoard[i][j + 1] == chess && chessBoard[i][j + 2] == chess) return 0
            }
        }
        for (i in (0..1)) {
            for (j in (0..3)) {
                if (chessBoard[i][j] == chess && chessBoard[i + 1][j] == chess && chessBoard[i + 2][j] == chess) return 0
            }
        }
        for (i in (0..1)) {
            for (j in (0..1)) {
                if (chessBoard[i][j] == chess && chessBoard[i + 1][j + 1] == chess && chessBoard[i + 2][j + 2] == chess) return 0
                if (chessBoard[i][j + 2] == chess && chessBoard[i + 1][j + 1] == chess && chessBoard[i + 2][j] == chess) return 0
            }
        }
        chessBoard.forEach { if (0 in it) return 1 }
        return 2
    }

    /*
    构建棋盘的图片.这里已经比较完善了,可能不需要改.
     */
    fun board(): InputStream {
        var background = blankBoard
        for (i in (0..3)) {
            loop@ for (j in (0..3)) {
                val chess = when (chessBoard[i][j]) {
                    1 -> black
                    2 -> white
                    else -> continue@loop
                }
                background = mergeImage(background, chess, i, j)
            }
        }
        return bufferedImageToInputStream(background)
    }


    private fun mergeImage(background: BufferedImage, chess: BufferedImage, x: Int, y: Int): BufferedImage {
        val wOfChess = chess.width
        val hOfChess = chess.height
        var chessArray = IntArray(wOfChess * hOfChess)
        chessArray = chess.getRGB(0, 0, wOfChess, hOfChess, chessArray, 0, wOfChess) // 逐行扫描图像中各个像素的RGB到数组中
        background.setRGB(200 * x, 200 * y, 200, 200, chessArray, 0, wOfChess)
        return background
    }

    private fun bufferedImageToInputStream(image: BufferedImage): InputStream {
        val os = ByteArrayOutputStream()
        ImageIO.write(image, "png", os)
        return ByteArrayInputStream(os.toByteArray())
    }
}