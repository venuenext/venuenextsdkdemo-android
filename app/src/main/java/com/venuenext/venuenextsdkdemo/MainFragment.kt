package com.venuenext.venuenextsdkdemo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.venuenext.vncore.VenueNext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.lang.Exception

class MainFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val self = this

        GlobalScope.async {
            try {
                VenueNext.initialize("[SDK_KEY]", "[SDK_SECRET]", self.context!!).await()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    findNavController().navigate(R.id.action_main_to_stands)
                }
            }

            withContext(Dispatchers.Main) {
                findNavController().navigate(R.id.action_main_to_stands)
            }
        }
    }
}
