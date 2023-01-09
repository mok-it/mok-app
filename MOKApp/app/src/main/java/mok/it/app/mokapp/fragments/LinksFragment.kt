package mok.it.app.mokapp.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mok.it.app.mokapp.R

/**
 * A simple [Fragment] subclass.
 * Use the [LinksFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LinksFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_links, container, false)
    }

    private fun openLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }
}

/* //linkek
    var hirlevelUrl =
        "https://drive.google.com/drive/folders/1KJX4tPXiFGN1OTNMZkBqHGswRTVfLPsQ?usp=sharing"
    var konyvtarUrl =
        "https://docs.google.com/spreadsheets/d/1T3FX6U1sT4TJwREr07iWKW9WnszEcQ-3r0oemRbengs/edit?usp=sharing"
    var programUrl =
        "https://drive.google.com/drive/folders/1EpMYa0WS_Eb35zwb3gH8Fa3jWvMOG1sU?usp=sharing"
    var otletUrl = "https://forms.gle/BPy6PfyU9Cu94h688"
    var youtubeUrl = "https://www.youtube.com/channel/UCdbgdsdFDIwXVtAqC_i08Bw/playlistsPro"

    //alap doksik
    var alapszabUrl = "https://drive.google.com/file/d/1FDcbFJmJGQDOWCV0vmO-gNHzocbkOW3_/view"
    var szmszUrl = "https://drive.google.com/file/d/1cRTZDFpM98oj_fibKKytvnAZcSc3qSc5/view"
    var etikaiUrl = "https://drive.google.com/file/d/1YMVjOhzQzyBTLON-lvZ3v8633CMHmrHg/view"
    var afpUrl = "https://drive.google.com/file/d/1EXcD1wvQ_CrcHdoNZA9GL2djlzje86x8/view"

    //kisokosok
    var mentorUrl =
        "https://drive.google.com/drive/folders/1osdINz0MRLNqlnWMBoY415QaSRh-NhGq?usp=sharing"
    var feladatUrl =
        "https://docs.google.com/document/d/1l-Z1oVcufQWXo115jamMtOtT-g6LW_C5AfkU2CzLdTQ/edit?usp=sharing"
    var natUrl =
        "https://docs.google.com/spreadsheets/d/11MGsOTMfXXHUeLv9nOKQCFpwoySfWgEK/edit?usp=sharing&ouid=106667379271700078582&rtpof=true&sd=true"

    //nagykönyvek
    var taborUrl = "https://drive.google.com/file/d/1To4r-J7wpc0-YK-eVGIdwAfXOl-N-nos/view"
    var versenyUrl = "https://drive.google.com/file/d/1JqeqAnGtG-HWrdqdLiNMcKpfimlfmxXl/view"
    var logirintusUrl = "https://drive.google.com/file/d/1GVySzao--5ozaYuSdR0s0yo-n2rGuqp3/view"*/


/* <item android:title="Linkek">
        <menu>
            <item android:id="@+id/nav_hirlevel"
                android:icon="@drawable/ic_link"
                android:title="Hírlevelek"/>
            <item android:id="@+id/nav_konyvtar"
                android:icon="@drawable/ic_link"
                android:title="Könyvtár"/>
            <item android:id="@+id/nav_program"
                android:icon="@drawable/ic_link"
                android:title="Programadatbázis"/>
            <item android:id="@+id/nav_otlet"
                android:icon="@drawable/ic_link"
                android:title="Ötletláda"/>
            <item android:id="@+id/nav_youtube"
                android:icon="@drawable/ic_link"
                android:title="Youtube"/>
        </menu>
    </item>
    <item android:title="Alapdokumentumok">
        <menu>
            <item android:id="@+id/nav_alapszab"
                android:icon="@drawable/ic_link"
                android:title="Alapszabály"/>
            <item android:id="@+id/nav_szmsz"
                android:icon="@drawable/ic_link"
                android:title="SZMSZ"/>
            <item android:id="@+id/nav_etikai"
                android:icon="@drawable/ic_link"
                android:title="Etikai kódex"/>
            <item android:id="@+id/nav_afp"
                android:icon="@drawable/ic_link"
                android:title="AFP"/>
        </menu>
    </item>
    <item android:title="Kisokosok">
        <menu>
            <item android:id="@+id/nav_mentor"
                android:icon="@drawable/ic_link"
                android:title="Mentor"/>
            <item android:id="@+id/nav_feladat"
                android:icon="@drawable/ic_link"
                android:title="Feladatbeküldés"/>
            <item android:id="@+id/nav_nat"
                android:icon="@drawable/ic_link"
                android:title="NAT"/>
        </menu>
    </item>
    <item android:title="Nagykönyvek">
        <menu>
            <item android:id="@+id/nav_tabor"
                android:icon="@drawable/ic_link"
                android:title="Tábori"/>
            <item android:id="@+id/nav_verseny"
                android:icon="@drawable/ic_link"
                android:title="Verseny"/>
            <item android:id="@+id/nav_logirintus"
                android:icon="@drawable/ic_link"
                android:title="Logirintus"/>
        </menu>
    </item>
    */