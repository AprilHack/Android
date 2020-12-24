package com.liu.game2.gameDao

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Message
import android.util.DisplayMetrics
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.liu.game2.PlayGame
import com.liu.game2.R
import com.liu.game2.gameDomin.Padel
import java.text.SimpleDateFormat
import java.util.*

class PadelDao(private val man: ImageView, private val context: Context, private val layout: RelativeLayout,
               private val dm: DisplayMetrics, downTime:Int, addPadelTime:Int, private val levelName:String   //难度级别名称
):Runnable {
    private var MEN_DEAD = -1
    private var running = true //控制线程的运转
    private var suspen = false //暂停游戏开关
    private var gameStartTime:Long = 0  //游戏开始时间记录器
    private var suspenTime:Long = 0
    private var continueTime:Long = 0
    private val padelsList = ArrayList<Padel>()
    private var index:Padel? = null
    private var scoreCount = 0
    private val textCount: TextView
    private val sd: SimpleDateFormat

    private val imgHeight:Int
    private val imgWidth:Int
    private val random: Random

    /**
     * 接受子线程发来的消息，并且调用相应的方法执行更新UI操作
     */
    private val handler = object: Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.what == PADEL_ADD)
                addPadel()
            MovePadal()
        }
    }

    init{
        DOWN_TIME = downTime
        ADD_PEDAL_TIME = addPadelTime
        textCount = layout.findViewById(R.id.textCount)
        textCount.text = "第" + scoreCount + "层"

        random = Random()
        sd = SimpleDateFormat("mm" + "分" + "ss" + "秒")

        val options = BitmapFactory.Options()
        BitmapFactory.decodeResource(context.resources, R.mipmap.taban, options)
        //获取图片的宽高
        imgHeight = options.outHeight
        imgWidth = options.outWidth
    }

    /**
     * 程序运行初始化踏板和小人位置
     */
    fun initPadel() {
        addPadel()

        val p = padelsList[0]
        index = p
        val x = p.getX() + (p.getLength() / 2)
        val mom = (dm.heightPixels / PadelDao.PADEL_SPEED)
        man.x = x.toFloat()             //小人的位置XY坐标初始化
        man.y = (mom * (PadelDao.PADEL_SPEED / 2)).toFloat()
        gameStartTime = System.currentTimeMillis()
    }

    /*public static void setLayout(View view, int x, int y)
       {
           ViewGroup.MarginLayoutParams margin=new ViewGroup.MarginLayoutParams(view.getLayoutParams());
           margin.setMargins(x,y, x+60, y+100);
           RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
           view.setLayoutParams(layoutParams);
       }

       public static void setLayoutY(View view,int y)
       {
           ViewGroup.MarginLayoutParams margin=new ViewGroup.MarginLayoutParams(view.getLayoutParams());
           margin.setMargins(margin.leftMargin,y, margin.rightMargin, y+margin.height);
           RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
           view.setLayoutParams(layoutParams);
       }*/

    /**
     * 随机生产踏板
     */
    fun addPadel() {
        var length = 0
//        val x:Int
        var x:Int
        val y = (dm.heightPixels / PADEL_SPEED) * PADEL_SPEED
        while (true) {
            length = random.nextInt(imgWidth - (imgWidth / 3)) + (imgWidth / 3)
            x = random.nextInt(dm.widthPixels - 50) + 50
            if (x + length < dm.widthPixels)
                break
        }
        val lvPadel = ImageView(context)
        val p = RelativeLayout.LayoutParams(length, 50)
        layout.addView(lvPadel, p)
        lvPadel.setImageResource(R.mipmap.taban)
        lvPadel.scaleType = ImageView.ScaleType.FIT_XY //不按比例缩放图片
        lvPadel.x = x.toFloat()
        lvPadel.y = y.toFloat()
        padelsList.add(Padel(length, x, y, lvPadel))
    }

    fun MovePadal() {    //踏板移动方法
        for (i in padelsList.indices) {
            if (padelsList[i].getY() === 0) {
                layout.removeView(padelsList[i].getIvPadel())
                padelsList.remove(padelsList[i])
                scoreCount++
                textCount.text = "第" + scoreCount + "层"
            } else {
                val p = padelsList[i]
                val lv = p.getIvPadel()
                p.getIvPadel().setY((p.getY() - PADEL_SPEED).toFloat())
                p.setY(p.getY() - PADEL_SPEED)
                isAtOutDrop(p.getX(), p.getY(), p.getLength(), p)
            }
        }
        isMenDead()
        moveMen()
    }

    /**
     * 小人的移动方法
     */
    fun moveMen() {
        val isDrop:Boolean
        val y = man.y.toInt()
        //有变动
        isDrop = isAtOutDrop(index!!.getX(), index!!.getY(), index!!.getLength(), index!!)
        if (isDrop) {
            man.y = (y + MEN_OUT_DROP_SPEED).toFloat()
        }
        else
            man.y = (y - PADEL_SPEED).toFloat()
    }

    /**
     * 判断小人是否掉下
     * @param x 判断条件的X坐标
     * @param y 判断条件的Y坐标
     * @param width 判断条件的踏板长度
     * @return 返回是否掉下 false表明没有掉落，true代表掉下
     */
    fun isAtOutDrop(x:Int, y:Int, width:Int, i: Padel):Boolean {
        val manX = man.x.toInt()
        val manY = man.y.toInt()
        val manHeight = manY + man.height

        if ((manX + man.width - 5) >= x && (manX - 5) <= (x + width)) {
            //如果X左边没有掉落的话
            if ((y == (manHeight + PADEL_SPEED) || y == manHeight || y == (manHeight + MEN_OUT_DROP_SPEED)
                        || (y + MEN_OUT_DROP_SPEED >= manHeight && (manHeight >= y || manHeight == y - 2)))) {
                //如果Y坐标相等的话
                index = i
                return false
            }
        }
        return true
    }


    /**
     * 判断小人是否死亡
     */
    private fun isMenDead() {
        val screen = (dm.heightPixels / PADEL_SPEED) * PADEL_SPEED
        val y = man.y.toInt()
        if (!(y < screen && y > 10) && MEN_DEAD == -1) {
            MEN_DEAD = 1
            suspen = true
            val gameEndTime = System.currentTimeMillis()
            val finalGameScore = gameEndTime - gameStartTime - continueTime
            val date = sd.format(Date(finalGameScore))

            //GameScoreData类--暂时注释
            //        GameScoreData.scoreData.add(GameScoreData(scoreCount, date, levelName)) //存储成绩

            //音乐
            PlayGame.mp!!.pause()

            AlertDialog.Builder(context).setTitle("Game Over !").setMessage(("您在：" + date + " 下了：" +
                    scoreCount + "层\n差点就破纪录啦！")).setPositiveButton("返回主菜单"
            ) { dialogInterface, i ->
                //返回主菜单的操作
                running = false
                //音乐
                if (PlayGame.mp != null) {//释放掉音乐资源
                    PlayGame.mp!!.release()
                    PlayGame.mp = null
                }
                //存储--暂时注释
                //            try {  //存储信息
                //                val os = context.openFileOutput("Score", Context.MODE_PRIVATE)
                //                GameScoreData.Sort() //先排序
                //                GameScoreData.saveScoreData(os)
                //            } catch (e: FileNotFoundException) {
                //                e.printStackTrace()
                //            }

                restore()
                (context as Activity).finish()
            }.setCancelable(false).setNegativeButton("重新玩", object: DialogInterface.OnClickListener {
                override fun onClick(dialogInterface: DialogInterface, i:Int) { //重新初始化游戏的操作
                    restore()
                    suspen = false
                    //音乐
                    PlayGame.mp!!.start()
                }
            }).show()
        }
    }


    fun restore() {   //还原游戏初始化状态
        for (padel in padelsList) {
            layout.removeView(padel.getIvPadel())
        }
        padelsList.clear()
        scoreCount = 0
        textCount.text = "第" + scoreCount + "层"
        initPadel()
//        PlayGame.menX = man.x
        PlayGame.menX = man.getX();
        MEN_DEAD = -1
    }

    /**
     * 暂停游戏
     */
    fun suspendGame() {
        suspenTime = System.currentTimeMillis()
        suspen = true
    }

    /**
     * 继续游戏
     */
    fun continueGame() {
        continueTime = System.currentTimeMillis() - suspenTime
        suspen = false
    }


    override fun run() {
        var count = 0
        while (running) {
            while (suspen) ;  //阻塞线程达到暂停的方法
            try {
                Thread.sleep( DOWN_TIME.toLong() )
                handler.sendEmptyMessage(PADEL_MOVE)
                if (count == ADD_PEDAL_TIME) {
                    handler.sendEmptyMessage(PADEL_ADD)
                    count = 0
                }
                count++
            } catch (e:InterruptedException) {}
        }
    }


    companion object {
        private val PADEL_ADD = 0 //增加踏板命令
        private val PADEL_MOVE = 1    //移动踏板命令
        val PADEL_SPEED = 4 //踏板移动单位数量
        private val MEN_OUT_DROP_SPEED = PADEL_SPEED * 2 //小人掉落的速度单位数量
        var DOWN_TIME = 30  //踏板移动的速度控制
        var ADD_PEDAL_TIME = 60 //踏板移动多少次产生新的踏板控制
    }
}