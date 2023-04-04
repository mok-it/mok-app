package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element
import mok.it.app.mokapp.BuildConfig
import mok.it.app.mokapp.R

class AboutFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        return AboutPage(context)
            .setImage(R.drawable.ic_logo_foreground)
            .setDescription(getString(R.string.about_description))
            .addItem(Element().setTitle("Verzió: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"))
            .addGroup("Elérhetőségeink")
            .addWebsite("https://www.mokegyesulet.hu/", "Egyesületi weboldal")
            .addPlayStore("mok.it.app.mokapp", "Play Store")
            .addGitHub("mok-it", "GitHub")
            .addGroup("Írd meg, ha hibát találtál vagy ötleted van!")
            .addWebsite("https://forms.gle/gcY7XgyiYaNBoGNf9", "Form")
            .create()
    }
}