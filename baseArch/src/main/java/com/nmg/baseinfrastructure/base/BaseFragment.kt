package com.nmg.baseinfrastructure.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


abstract class BaseFragment < D : ViewDataBinding>:Fragment() {

    var isShown: Boolean = false
    protected lateinit var binding: D



    @get:LayoutRes
    protected abstract val layoutRes: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        initUI(savedInstanceState)
        binding.executePendingBindings()
        return binding.root
    }
    protected abstract fun initUI(savedInstanceState: Bundle?)

    fun <VH : RecyclerView.ViewHolder> setUpRcv(rcv: RecyclerView, adapter: RecyclerView.Adapter<VH>) {
        rcv.setHasFixedSize(true)
        rcv.layoutManager = LinearLayoutManager(context)
        rcv.adapter = adapter
    }

    fun <VH : RecyclerView.ViewHolder> setUpRcv(
        rcv: RecyclerView, adapter:
        RecyclerView.Adapter<VH>,
        isHasFixedSize: Boolean,
        isNestedScrollingEnabled: Boolean
    ) {
        rcv.setHasFixedSize(isHasFixedSize)
        rcv.layoutManager = LinearLayoutManager(context)
        rcv.adapter = adapter
        rcv.isNestedScrollingEnabled = isNestedScrollingEnabled
    }

    fun <VH : RecyclerView.ViewHolder> setUpRcv(
        rcv: RecyclerView, adapter:
        RecyclerView.Adapter<VH>,
        isNestedScrollingEnabled: Boolean
    ) {
        rcv.setHasFixedSize(true)
        rcv.layoutManager = LinearLayoutManager(context)
        rcv.adapter = adapter
        rcv.isNestedScrollingEnabled = isNestedScrollingEnabled
    }

    fun addFragment(
        fragment: Fragment,
        id: Int,
        addToBackStack: Boolean
    ) {

        val activity = activity
        if(activity is BaseActivity<*>)
            activity.addFragment(fragment , id , addToBackStack)
    }

    fun addFragment(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        id: Int,
        addToBackStack: Boolean
    ) {

        val activity = activity
        if(activity is BaseActivity<*>)
            activity.addFragment(fragmentManager ,fragment , id , addToBackStack)
    }



    fun replaceFragment(
        fragment: Fragment,
        id: Int,
        addToBackStack: Boolean
    ) {

        val activity = activity
        if(activity is BaseActivity<*>)
            activity.replaceFragment(fragment , id , addToBackStack)
    }

    fun replaceFragment(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        id: Int,
        addToBackStack: Boolean
    ) {

        val activity = activity
        if(activity is BaseActivity<*>)
            activity.replaceFragment(fragmentManager ,fragment , id , addToBackStack)
    }


    fun openDialogFragment(dialogListFragment: BaseDialog<*>) {
        activity?.let {
            if (it is BaseActivity<*>) {
                it.openDialogFragment(dialogListFragment)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        isShown = false
    }


    override fun onResume() {
        super.onResume()

        isShown = true
    }



    fun onBackbressed(): Boolean {

        var beforelasFragment : Fragment? = null

        if(isShown){
            var count = childFragmentManager.backStackEntryCount

            while (count != 0){
                val name =  childFragmentManager.getBackStackEntryAt(count-1).name
                beforelasFragment = childFragmentManager.findFragmentByTag(name)
                count = beforelasFragment?.childFragmentManager?.backStackEntryCount?: 0
            }

            if(beforelasFragment != null) {
                childFragmentManager.popBackStack()
                return true
            }
        }

        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val count = childFragmentManager.backStackEntryCount

        for(i in 0 until count){
            val name = childFragmentManager.getBackStackEntryAt(i).name
            val frag = childFragmentManager.findFragmentByTag(name)
            if(frag is BaseFragment<*>)
                frag.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val count = childFragmentManager.backStackEntryCount

        for(i in 0 until count){
            val name = childFragmentManager.getBackStackEntryAt(i).name
            val frag = childFragmentManager.findFragmentByTag(name)
            if(frag is BaseFragment<*>)
                frag.onActivityResult(requestCode, resultCode, data)
        }
    }


}