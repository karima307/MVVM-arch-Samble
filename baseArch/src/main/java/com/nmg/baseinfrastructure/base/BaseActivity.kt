@file:Suppress("UNCHECKED_CAST")

package com.nmg.baseinfrastructure.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nmg.baseinfrastructure.utils.LocaleHelperJava


abstract class BaseActivity<T : ViewDataBinding> : AppCompatActivity() {
    lateinit var binding: T

    @get:LayoutRes
    protected abstract val layoutRes: Int

    protected abstract fun initUI(savedInstanceState: Bundle?)

    override fun onCreate(savedInstanceState: Bundle?) {
        LocaleHelperJava.onAttach(this)
        super.onCreate(savedInstanceState)
        performDataBinding()
        initUI(savedInstanceState)
    }


    protected open fun performDataBinding() {
        binding = DataBindingUtil.setContentView(this, layoutRes)
        binding.executePendingBindings()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelperJava.onAttach(newBase))
    }


    fun <VH : RecyclerView.ViewHolder> setUpRcv(
        rcv: RecyclerView,
        adapter: RecyclerView.Adapter<VH>
    ) {
        rcv.setHasFixedSize(true)
        rcv.layoutManager = LinearLayoutManager(this)
        rcv.adapter = adapter
    }

    fun <VH : RecyclerView.ViewHolder> setUpRcv(
        rcv: RecyclerView, adapter:
        RecyclerView.Adapter<VH>,
        isHasFixedSize: Boolean,
        isNestedScrollingEnabled: Boolean
    ) {
        rcv.setHasFixedSize(isHasFixedSize)
        rcv.layoutManager = LinearLayoutManager(this)
        rcv.adapter = adapter
        rcv.isNestedScrollingEnabled = isNestedScrollingEnabled
    }

    fun <VH : RecyclerView.ViewHolder> setUpRcv(
        rcv: RecyclerView,
        adapter: RecyclerView.Adapter<VH>,
        isNestedScrollingEnabled: Boolean
    ) {
        rcv.setHasFixedSize(true)
        rcv.layoutManager = LinearLayoutManager(this)
        rcv.adapter = adapter
        rcv.isNestedScrollingEnabled = isNestedScrollingEnabled
    }

    open fun clearAllBackStack() {
        val fm = supportFragmentManager
        for (i in 0 until fm.backStackEntryCount) {
            fm.popBackStack()
        }
    }

    abstract fun showSuccessMessage(message: String, onPositiveActionClickListener: () -> Unit)
    abstract fun showSuccessMessage(message: String, onPositiveActionClickListener: () -> Unit,onNegativeActionClickListener:()->Unit)
   /* abstract fun showErrorMessage(message: String, onPositiveActionClickListener: () -> Unit)
    abstract fun showErrorMessage(message: String, onPositiveActionClickListener: () -> Unit,onNegativeActionClickListener:()->Unit)
    */
   fun addFragment(fragment: Fragment, id: Int, addToBackStack: Boolean) {
        addFragment(supportFragmentManager, fragment, id, addToBackStack)
    }


    //Add Fragment by fragmentManager
    fun addFragment(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        id: Int,
        addToBackStack: Boolean
    ) {

        val transaction = fragmentManager.beginTransaction()

        transaction.add(id, fragment, fragment.javaClass.simpleName)

        if (addToBackStack)
            transaction.addToBackStack(fragment.javaClass.simpleName)

        transaction.commit()

    }

    fun replaceFragment(fragment: Fragment, id: Int, addToBackStack: Boolean) {
        replaceFragment(supportFragmentManager, fragment, id, addToBackStack)
    }

    fun replaceFragment(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        id: Int,
        addToBackStack: Boolean
    ) {

        val transaction = fragmentManager.beginTransaction()


        if (addToBackStack)
            transaction.addToBackStack(fragment.javaClass.canonicalName)

        transaction.replace(id, fragment, fragment.javaClass.canonicalName)
        transaction.commit()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        supportFragmentManager.fragments.forEach {
            it.onActivityResult(requestCode, resultCode, data)
        }
    }


    fun openDialogFragment(dialogListFragment: BaseDialog<*>) {
        val ft = supportFragmentManager.beginTransaction()
        dialogListFragment.show(ft, "dialog")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        supportFragmentManager.fragments.forEach {
            it.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }


}