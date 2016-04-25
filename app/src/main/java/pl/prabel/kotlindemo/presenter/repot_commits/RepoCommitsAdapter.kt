package pl.prabel.kotlindemo.presenter.repot_commits

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.repo_commit_item.view.*
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.onClick
import pl.prabel.kotlindemo.R
import pl.prabel.kotlindemo.api.CommitModel
import rx.functions.Action1

class RepoCommitsAdapter
constructor(val itemClick: (CommitModel) -> Unit) :
        RecyclerView.Adapter<RepoCommitsAdapter.ViewHolder>(),
        Action1<List<CommitModel>> {

    var items = emptyList<CommitModel>()

    class ViewHolder(itemView: View, val itemClick: (CommitModel) -> Unit) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: CommitModel) {

            itemView.title.text = item.commit.message
            itemView.comment_count.text = item.commit.comment_count.toString()

            itemView.onClick { itemClick(item) }
        }
    }

    override fun onBindViewHolder(holder: RepoCommitsAdapter.ViewHolder?, position: Int) {
        holder!!.bind(items.get(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RepoCommitsAdapter.ViewHolder?
            = RepoCommitsAdapter.ViewHolder(parent!!.context.layoutInflater.inflate(R.layout.repo_commit_item, parent, false), itemClick)

    override fun getItemCount(): Int = items.size

    override fun call(newItems: List<CommitModel>?) {
        items = newItems!!
        notifyDataSetChanged()
    }
}