package pl.prabel.kotlindemo.presenter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.repo_item.view.*
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.onClick
import pl.prabel.kotlindemo.R
import pl.prabel.kotlindemo.api.RepoModel
import rx.functions.Action1
import javax.inject.Inject

class RepositoriesAdapter
constructor(val itemClick: (RepoModel) -> Unit) :
        RecyclerView.Adapter<RepositoriesAdapter.ViewHolder>(),
        Action1<List<RepoModel>> {

    var items = emptyList<RepoModel>()

    class ViewHolder(itemView: View, val itemClick: (RepoModel) -> Unit) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: RepoModel) {

            itemView.title.text = item.name
            itemView.description.text = item.description
            itemView.stars.text = item.stargazersCount

            itemView.onClick { itemClick(item) }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder!!.bind(items.get(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder?
            = ViewHolder(parent!!.context.layoutInflater.inflate(R.layout.repo_item, parent, false), itemClick)

    override fun getItemCount(): Int = items.size

    override fun call(newItems: List<RepoModel>?) {
        items = newItems!!
        notifyDataSetChanged()
    }
}
