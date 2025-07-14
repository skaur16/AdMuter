package com.example.admuter

import android.app.Notification
import android.media.AudioManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import android.widget.Toast

class SpotifyListenerService : NotificationListenerService() {

    override fun onNotificationPosted(sbn : StatusBarNotification?){

        sbn?.let{sbn->
            if(sbn.packageName == "com.spotify.music"){
                val notification = sbn.notification
                Log.e("SPOTIFY NOTIFICATION", notification.toString())
                val extras = notification.extras
                Log.e("SPOTIFY EXTRAS", extras.toString())
                val title = extras.get("android.title").toString()
                Log.e("SPOTIFY TITLE2", title)
                val text = extras.get("android.text").toString()
                Log.e("SPOTIFY TEXT", text)

                if(isAd(title,text)){
                    muteDevice()
                }
                else{
                    unmuteDevice()
                }
            }
        } ?: Log.e("DATA", "No StatusBarNotificationFound")
    }

    private fun isAd(title : String?, text : String?) : Boolean{
        return title.equals("Advertisement", ignoreCase = true)
                || title.equals(null, ignoreCase = true)
                || text.equals("Spotify", ignoreCase = true)
                || text.equals(null, ignoreCase = true)
                || text.equals("Advertisement", ignoreCase = true)
    }



    private fun muteDevice(){
       val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0)
        Log.e("STATUS", "MUTED")

    }

    private fun unmuteDevice(){
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE,0)
        Log.e("STATUS", "UNMUTED")

    }



    override fun onListenerConnected() {
        Log.e("Connection_Status", "Connected" )
        val currentNotifications = activeNotifications
        currentNotifications.forEach {
            if (it.packageName == "com.spotify.music") {
                val notification = it.notification
                val extras = notification.extras
                val title = extras.get("android.title").toString()
                val text = extras.get("android.text").toString()
                if (isAd(title, text)) {
                    muteDevice()
                } else {
                    unmuteDevice()
                }

            }
        }
    }

        override fun onListenerDisconnected() {
            unmuteDevice()
            Log.e("Connection_Status", "DisConnected" )

        }

        fun showToast(message : String){
            val handler = android.os.Handler(mainLooper)
            handler.post{
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

}