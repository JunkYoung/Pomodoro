package com.junkyoung.life

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        val pref = getSharedPreferences("db",0)
        val editor = pref.edit()

        var job = Job()
        val handler = Handler()

        var strList = pref.getString("List", "").toString()
        var list: MutableList<String> = strList.split(' ').toMutableList()
        textView_list.text = strList

        var select = 25
        selectSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                select = 5
            } else {
                select = 25
            }
        }

        fun View.blink(
            times: Int = Animation.INFINITE,
            duration: Long = 50L,
            offset: Long = 20L,
            minAlpha: Float = 0.0f,
            maxAlpha: Float = 1.0f,
            repeatMode: Int = Animation.REVERSE
        ) {
            startAnimation(AlphaAnimation(minAlpha, maxAlpha).also {
                it.duration = duration
                it.startOffset = offset
                it.repeatMode = repeatMode
                it.repeatCount = times
            })
        }

        fun count25() {
            job = GlobalScope.launch(Dispatchers.IO) {
                while (isActive) {
                    for (i in 1 until (list.size)) {
                        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                        textView_todo.blink(0, 2000L, 0L, 1.0f, 0.0f)
                        delay(2000)
                        handler.post {
                            textView_todo.text = list[i]
                            textView_todo.blink(0, 2000L, 0L, 0.0f, 1.0f)
                        }
                        delay(25 * 600)
                        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                        textView_todo.blink(0, 2000L, 0L, 1.0f, 0.0f)
                        delay(2000)
                        handler.post {
                            textView_todo.text = "휴식"
                            textView_todo.blink(0, 2000L, 0L, 0.0f, 1.0f)
                        }
                        delay(5 * 600)
                    }
                }
            }
        }

        fun count5() {
            job = GlobalScope.launch(Dispatchers.Main) {
                while (isActive) {
                    for (i in 1 until (list.size)) {
                        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                        textView_todo.blink(0, 2000L, 0L, 1.0f, 0.0f)
                        delay(2000)
                        handler.post {
                            textView_todo.text = list[i]
                            textView_todo.blink(0, 2000L, 1000L, 0.0f, 1.0f)
                        }
                        delay(5 * 600)
                    }
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                    textView_todo.blink(0, 2000L, 0L, 1.0f, 0.0f)
                    delay(2000)
                    handler.post {
                        textView_todo.text = "휴식"
                        textView_todo.blink(0, 2000L, 1000L, 0.0f, 1.0f)
                    }
                    delay(5 * 600)
                }
            }
        }

        var button_state = 0
        playButton.setOnClickListener {
            if (button_state == 1) {
                playButton.setBackgroundResource(R.drawable.button_play)
                button_state = 0
                editText.visibility = View.VISIBLE
                GlobalScope.launch {
                    job.cancelAndJoin()
                }
            }
            else {
                playButton.setBackgroundResource(R.drawable.button_paused)
                button_state = 1
                editText.visibility = View.INVISIBLE
                if (select == 25) {
                    count25()
                }
                else {
                    count5()
                }
            }
        }


        addButton.setOnClickListener {
            var todo = editText.text.toString()
            if (todo in strList) {
                list.remove(todo)
                strList = ""
                for (item in list) {
                    if (item != "") {
                        strList += ' ' + item
                    }
                }
                list = strList.split(' ').toMutableList()
                editor.putString("List", strList).apply()
                textView_list.text = strList
            }
            else {
                strList = strList + ' ' + todo
                list = strList.split(' ').toMutableList()
                editor.putString("List", strList).apply()
                textView_list.text = strList
            }
        }
    }
}

