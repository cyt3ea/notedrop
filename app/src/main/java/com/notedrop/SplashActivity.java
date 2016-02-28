package com.notedrop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;


public class SplashActivity extends Activity {

    static boolean load = false;
    private MediaPlayer logoMusic;

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("info",0);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().hide();
        setContentView(R.layout.splash);

        //logoMusic = MediaPlayer.create(MainActivity.this, R.raw.splash_sound);
        //logoMusic.start();
        Thread logoTimer = new Thread() {
            public void run() {
                try{
                    sleep(2350);
                    Intent menuIntent = new Intent("com.example.capp.MENU");
                    //logoMusic.stop();
                    startActivity(menuIntent);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally{
                    finish();
                }
            }
        };
        logoTimer.start();
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_day) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */

}
