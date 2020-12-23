package com.liu.game2

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import com.liu.game2.gameDao.PadelDao

class PlayGame: Activity(), View.OnClickListener {
    private var x:Int = 0
    var menX: Float = 0.toFloat()
    private lateinit var man: ImageView
    private lateinit var padel: PadelDao
    private lateinit var layout: RelativeLayout
    private lateinit var dm: DisplayMetrics
    private lateinit var btnSuspen: ImageButton
    private lateinit var sp: SoundPool

    private var songID:Int = 0
    private var moveTime:Int = 0
    private var addPadelTime:Int = 0
    private var menName:Int = 0
    //    private var levelName:String?=null
    private lateinit var levelName:String
//    private var levelName:String
//    音乐--暂时注释
//    var mp: MediaPlayer? = null

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        val data = intent.getBundleExtra("data")
        moveTime = data!!.getInt("Level_MoveTime")
        addPadelTime = data!!.getInt("Level_addPadelTime")
        //改
        levelName = data!!.getString("Level_Name").toString()
//        levelName = data!!.getString("Level_Name")
        when (data!!.getString("MenName")) {
            "石乐志" -> menName = R.mipmap.men1
            "神经质" -> menName = R.mipmap.men2
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            //透明导航栏
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        //音乐--暂时注释
//        sp = SoundPool(0, AudioManager.STREAM_MUSIC, 0)
//        songID = sp!!.load(this, R.raw.key_music, 1)
//        mp = MediaPlayer.create(this, R.raw.b_music)  //初始化音乐资源
//        mp!!.start()
//            mp!!.isLooping = true

        btnSuspen = findViewById(R.id.btnGameSuspen)
        btnSuspen!!.setOnClickListener(this)
        layout = findViewById(R.id.lyoutGame)
        dm = resources.displayMetrics
        x = dm!!.widthPixels / 2
        man = ImageView(this)
        man!!.setImageResource(menName)
        //man.setScaleType(ImageView.ScaleType.FIT_CENTER);
        man!!.layoutParams = RelativeLayout.LayoutParams(60, 100)
        man!!.scaleType = ImageView.ScaleType.FIT_XY
        init()
        menX = man!!.x
        layout!!.addView(man)
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.x > x) {  //右边
            if (menX + man!!.getWidth() < dm!!.widthPixels) {
                menX += 10f
                man!!.setTranslationX(menX)
            }
//                man!!.setTranslationX(menX += 10f)
        }
        if (event.x < x) {  //左边
            if (menX > 0) {
                // man!!.setTranslationX(menX -= 10f)
                menX -= 10f
                man!!.setTranslationX(menX)
            }

        }
        return true
    }


    fun init() {
        padel = PadelDao(man, this, layout, dm, moveTime, addPadelTime, levelName)
        padel!!.initPadel()
        Thread(padel).start()
    }



    override fun onRestart() {
        mp!!.start()
        padel!!.continueGame()
        super.onRestart()
    }

    override fun onPause() {
        if (mp != null) mp!!.pause()
        super.onPause()
    }

    override fun onStop() {
        padel!!.suspendGame()
        super.onStop()
    }


    override fun onClick(view: View) {  //用户点击了暂停按钮
        sp!!.play(songID, 1f, 1f, 0, 0, 1f)
        padel!!.suspendGame()
        mp!!.pause()
        AlertDialog.Builder(this).setTitle("暂停").setMessage("暂停了游戏").setPositiveButton("返回主菜单"
        ) { dialogInterface, i ->
            mp!!.release()
            mp = null
            finish()
        }.setNegativeButton("继续游戏", object: DialogInterface.OnClickListener {
            override fun onClick(dialogInterface: DialogInterface, i:Int) {
                padel!!.continueGame()
                mp!!.start()
            }
        }).setCancelable(false).show()
    }

    override fun onBackPressed() {   //用户按下返回键的监听事件
        padel!!.suspendGame()
        mp!!.pause()
        AlertDialog.Builder(this).setTitle("暂停").setMessage("暂停了游戏").setPositiveButton("返回主菜单", object:
            DialogInterface.OnClickListener {
            override fun onClick(dialogInterface: DialogInterface, i:Int) {
                mp!!.release()
                mp = null
                finish()
            }
        }).setNegativeButton("继续游戏", object: DialogInterface.OnClickListener {
            override fun onClick(dialogInterface: DialogInterface, i:Int) {
                padel!!.continueGame()
                mp!!.start()
            }
        }).setCancelable(false).show()
    }

    companion object {
        var menX:Float = 0.toFloat()
        var mp: MediaPlayer? = null
    }
}

