package com.example.wheel_of_emotions

import android.animation.Animator
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val wheel = findViewById<ImageView>(R.id.imageView1) as ImageView
        val emotions = findViewById<ImageView>(R.id.imageView2) as ImageView
        wheel.setOnClickListener {
            rotateAnim(wheel, 90F)
            rotateAnim(emotions, 90F)
        }
    }

    fun rotateAnim(imageView: ImageView, angle : Float){
        imageView.isEnabled = false
        Log.i(TAG, "onCreate: ${imageView.rotation}")
        val rotation = imageView.animate().rotationBy(angle)
        rotation.interpolator = FastOutSlowInInterpolator()
        rotation.startDelay = 200
        rotation.setListener(object : Animator.AnimatorListener{
            override fun onAnimationEnd(animation: Animator?) {
                imageView.isEnabled = true
            }
            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationRepeat(animation: Animator?) {}

        })
        rotation.start()
    }


}