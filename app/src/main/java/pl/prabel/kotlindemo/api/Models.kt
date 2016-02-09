package pl.prabel.kotlindemo.api

import java.util.*


data class User(val id: String,
                val login: String,
                val avatarUrl: String,
                val htmlUrl: String)

data class IssueCommentModel(val user: User,
                             val body: String,
                             val createdAt: Date)

data class RepoOwner(val login: String,
                     val id: String)

data class RepoModel(val id: String,
                     val name: String,
                     val fullName: String,
                     val description: String,
                     val stargazersCount: String,
                     val repoOwner: RepoOwner,
                     val openIssues: Int)

data class IssueModel(val id: String,
                      val title: String,
                      val body: String,
                      val createdAt: Date,
                      val user: User)
