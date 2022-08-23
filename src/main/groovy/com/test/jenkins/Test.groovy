package com.test.jenkins

import com.jcabi.github.Coordinates
import com.jcabi.github.FromProperties
import com.jcabi.github.Repo
import com.jcabi.github.RtGithub
import com.jcabi.github.mock.MkGithub
import com.jcabi.http.request.ApacheRequest
import com.jcabi.http.wire.AutoRedirectingWire

import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.MediaType

class Test {
    static void main(String[] args) {
        def request = new ApacheRequest("https://google.com")
                .header(HttpHeaders.USER_AGENT, new FromProperties("jcabigithub.properties").format())
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.RETRY_AFTER, 30)
                .through(AutoRedirectingWire.class)
                .header(HttpHeaders.AUTHORIZATION, String.format("token %s", ""))
        def github = new RtGithub(request)
        github.limits()
        def githubq = new MkGithub(request)
        Repo repo = github.repos().get(new Coordinates.Simple("organisation", "name"))
        repo.pulls().create("", "", "")
    }
}
