package com.demo.demogridphoto.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import com.demo.demogridphoto.R
import com.demo.demogridphoto.askPermission
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Made by Rein Lin on 2020.4.6
 */
class MainActivity : Activity() {

    companion object {
        const val RequestCode_NextPage      = 1
        const val RequestCode_AskPermission = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        println("onCreate")

        nextBtn.setOnClickListener {
            this.askPermission(RequestCode_AskPermission) {
                nextPage()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println("onActivityResult $requestCode, $resultCode")

        when(requestCode) {
            RequestCode_NextPage -> println("Back Home Page: $resultCode")
        }
    }

    override fun onStart() {
        super.onStart()
        println("onStart")
    }

    override fun onResume() {
        super.onResume()
        println("onResume")
    }

    override fun onPause() {
        super.onPause()
        println("onPause")
    }

    override fun onStop() {
        super.onStop()
        println("onStop")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        println("onRequestPermissionsResult: $requestCode, ${grantResults.size}")
        when (requestCode) {
            RequestCode_AskPermission ->
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) nextPage()
        }
    }

    private fun nextPage() {
        val intent = Intent(this, GridActivity::class.java)
        startActivityForResult(intent,
            RequestCode_NextPage
        )
    }

}
