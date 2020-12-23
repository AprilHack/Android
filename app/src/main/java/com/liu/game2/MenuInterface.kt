package com.liu.game2

import android.app.Activity
import android.content.Intent
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.Nullable
import java.io.FileNotFoundException

class MenuInterface : Activity(), View.OnClickListener {

    private var sp: SoundPool? = null
    private var songID: Int = 0
//    private var dialogSetting: DialogSetting? = null
    private var menName = "石乐志"
    private var level = "小儿科"
    private var moveTime: Int = 0   //踏板移动速度
    private var addPadelTime: Int = 0//生成踏板的时间单位

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //暂时注释
//        try {
//            val `is` = openFileInput("Score")
//            GameScoreData.readScoreData(`is`)
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//        }

        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }

        sp = SoundPool(0, AudioManager.STREAM_MUSIC, 0)
        //暂时注释
//        songID = sp!!.load(this, R.raw.key_music, 1)
//暂时注释
//        val btnPlay = findViewById(R.id.btnPlay)
//        val btnSetting = findViewById(R.id.btnSetting)
//        val btnScore = findViewById(R.id.btnScore)
//        val btnExit = findViewById(R.id.btnExit)
//暂时注释
//        btnPlay.setOnClickListener(this)
//        btnSetting.setOnClickListener(this)
//        btnScore.setOnClickListener(this)
        btnExit.setOnClickListener(this)

    }

    private fun setGameLevelAndMen() {   //难度修改
        when (level) {
            "小儿科" -> {
                moveTime = 12
                addPadelTime = 150
            }
            "老年痴呆" -> {
                moveTime = 8
                addPadelTime = 180
            }
            "精神病" -> {
                moveTime = 5
                addPadelTime = 250
            }
            "疯人院" -> {
                moveTime = 3
                addPadelTime = 200
            }
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnPlay -> {
                sp!!.play(songID, 1f, 1f, 0, 0, 1f)
                setGameLevelAndMen()
                val data = Bundle()
                val intent = Intent(this, PlayGame::class.java)
                data.putInt("Level_MoveTime", moveTime)
                data.putInt("Level_addPadelTime", addPadelTime)
                data.putString("MenName", menName)
                data.putString("Level_Name", level)
                intent.putExtra("data", data)
                startActivity(intent)
            }
            //暂时注释
//            R.id.btnSetting -> {
//                sp!!.play(songID, 1f, 1f, 0, 0, 1f)
//                dialogSetting = DialogSetting(this, 300, 500)
//                dialogSetting!!.setCancelable(false)
//                dialogSetting!!.show()
//                dialogSetting!!.setButtobSaveListener(View.OnClickListener {
//                    menName = dialogSetting!!.getMenName()
//                    level = dialogSetting!!.getLevelName()
//                    dialogSetting!!.dismiss()
//                })
//            }
//            R.id.btnScore -> {
//                sp!!.play(songID, 1f, 1f, 0, 0, 1f)
//                val dialogScore = DialogScore(this, 300, 500)
//                for (g in GameScoreData.scoreData) {
//                    dialogScore.addListData(
//                        String.format(
//                            "%s%s%s%s%s",
//                            g.getLevel(),
//                            " 成绩：",
//                            g.getScoreCount(),
//                            "层 时间：",
//                            g.getScoreDate()
//                        )
//                    )
//                }
//                dialogScore.show()
//            }
            R.id.btnExit -> {
                sp!!.play(songID, 1f, 1f, 0, 0, 1f)
                finish()
            }
        }
    }
}
