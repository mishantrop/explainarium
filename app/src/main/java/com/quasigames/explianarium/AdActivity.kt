package com.quasigames.explainarium

import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_ad.*

private const val START_LEVEL = 1

class AdActivity : AppCompatActivity() {
    private var mLoadAdButton: Button? = null
    private var mInterstitialAd: InterstitialAd? = null
    private var currentLevel: Int = 0
    private var interstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad)
        mLoadAdButton = findViewById<Button>(R.id.next_level_button)
        mLoadAdButton!!.isEnabled = false
        mLoadAdButton!!.setOnClickListener {
            showInterstitial()
        }
        mInterstitialAd = newInterstitialAd()
        loadInterstitial()
    }

    private fun newInterstitialAd(): InterstitialAd {
        val interstitialAd = InterstitialAd(this)
        interstitialAd.adUnitId = getString(R.string.interstitial_ad_unit_id_debug)
        interstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {
                mLoadAdButton!!.isEnabled = true
                Toast.makeText(applicationContext, "Ad Loaded", Toast.LENGTH_SHORT).show()
            }
            override fun onAdFailedToLoad(errorCode: Int) {
                mLoadAdButton!!.isEnabled = true
                Toast.makeText(applicationContext, "Ad Failed To Load", Toast.LENGTH_SHORT).show()
            }
            override fun onAdClosed() {
                Toast.makeText(applicationContext, "Ad Closed", Toast.LENGTH_SHORT).show()
                tryToLoadAdOnceAgain()
            }
        }
        return interstitialAd
    }

    private fun tryToLoadAdOnceAgain() {
        mInterstitialAd = newInterstitialAd()
        loadInterstitial()
    }

    private fun showInterstitial() {
        if (mInterstitialAd != null && mInterstitialAd!!.isLoaded) {
            mInterstitialAd!!.show()
        } else {
            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show()
            tryToLoadAdOnceAgain()
        }
    }

    private fun loadInterstitial() {
        mLoadAdButton!!.isEnabled = false
        val adRequest = AdRequest.Builder().build()
        mInterstitialAd!!.loadAd(adRequest)
    }

    private fun goToNextLevel() {
        level.text = "Level " + (++currentLevel)
        interstitialAd = newInterstitialAd()
        loadInterstitial()
    }
}
