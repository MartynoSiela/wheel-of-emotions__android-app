package com.example.wheel_of_emotions

import android.animation.Animator
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.interpolator.view.animation.FastOutLinearInInterpolator

// This MainActivity example accompanies OnSwipeTouchListenerV1
class MainActivityV1 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val wheel = findViewById<ImageView>(R.id.imageView1) as ImageView
        val emotions = findViewById<ImageView>(R.id.imageView2) as ImageView

        window.decorView.setOnTouchListener(object: OnSwipeTouchListenerV1() {

            override fun onSwipeLeft(diffX: Float) {
                // Attempt to make it work on slow/short swipes
                val angle = if (diffX > 50) diffX * 2 else diffX * 10
                rotateAnim(wheel, angle)
                rotateAnim(emotions, angle)
            }

            override fun onSwipeRight(diffX: Float) {
                // Attempt to make it work on slow/short swipes
                val angle = if (diffX > 50) diffX * 2 else diffX * 10
                rotateAnim(wheel, angle)
                rotateAnim(emotions, angle)
            }
        })
    }

    // Basic rotation action
    fun rotateImage(imageView: ImageView, angle: Float) {
        imageView.animate().rotation(angle).start()
    }

    // Found here: https://stackoverflow.com/a/71615594/4054411
    fun rotateAnim(imageView: ImageView, angle : Float){
        imageView.isEnabled = false
        val rotation = imageView.animate().rotation(angle)
        // TODO try AccelerateDecelerateInterpolator
        // https://stackoverflow.com/a/70368936/4054411
        rotation.interpolator = FastOutLinearInInterpolator()
        // disabled delay as it is not natural
        // rotation.startDelay = 0
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