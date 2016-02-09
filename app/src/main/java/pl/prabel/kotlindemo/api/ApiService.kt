package pl.prabel.kotlindemo.api

import retrofit.http.GET
import retrofit.http.Path
import rx.Observable

interface ApiService {

    @GET("/authorizations")
    fun authorizations(): Observable<Any>

    @GET("/user/repos")
    fun getRepositories(): Observable<List<RepoModel>>

    @GET("/repos/{user}/{repo}/issues")
    fun getIssuesForRepo(@Path("user") user: String, @Path("repo") repoName: String)

    @GET("/repos/{user}/{repo}/issues/{issue}/comments")
    fun getCommenForIssue(@Path("user") user: String,
                          @Path("repo") repoName: String,
                          @Path("issue") issueNumber: String)
}
