package pl.prabel.kotlindemo.extensions

import android.app.Activity
import android.support.design.widget.Snackbar
import android.view.View

public fun Activity.snackBar(message: CharSequence,
                             contentView: View = this.findViewById(android.R.id.content),
                             duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(contentView, message, duration).show()
}

public fun View.hide(){
    this.visibility = View.GONE
}

public fun View.show(){
    this.visibility = View.VISIBLE
}
