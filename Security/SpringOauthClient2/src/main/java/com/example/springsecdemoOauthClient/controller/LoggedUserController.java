package com.example.springsecdemoOauthClient.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoggedUserController {
	
	
	/** ESTE NO ES EL RS.. es para demostrar que se guardó en el contexto de Spring un objeto que representa al usuario, pero esta app lo obtuvo del RS. Y el cliente web le pide a esta que le dé algún dato.
	 			The app you just wrote, in OAuth 2.0 terms, is a Client Application, and it uses the authorization code grant to obtain an access token from GitHub (the Authorization Server).
				It then uses the access token to ask GitHub for some personal details (only what you permitted it to do), including your login ID and your name. In this phase, GitHub is acting as a Resource Server, decoding the token that you send and checking if it gives the app permission to access the user’s details. If that process is successful, the app inserts the user details into the Spring Security context so that you are authenticated.
	 * */
	@GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
		// It’s not a great idea to return a whole OAuth2User in an endpoint since it might contain information you would rather not reveal to a browser client. 
		System.out.println(principal);
        // Name: [64266774], Granted Authorities: [[ROLE_USER, SCOPE_read:user]], User Attributes: [{login=lsarot, id=64266774, node_id=MDQ6VXNlcjY0MjY2Nzc0, avatar_url=https://avatars0.githubusercontent.com/u/64266774?v=4, gravatar_id=, url=https://api.github.com/users/lsarot, html_url=https://github.com/lsarot, followers_url=https://api.github.com/users/lsarot/followers, following_url=https://api.github.com/users/lsarot/following{/other_user}, gists_url=https://api.github.com/users/lsarot/gists{/gist_id}, starred_url=https://api.github.com/users/lsarot/starred{/owner}{/repo}, subscriptions_url=https://api.github.com/users/lsarot/subscriptions, organizations_url=https://api.github.com/users/lsarot/orgs, repos_url=https://api.github.com/users/lsarot/repos, events_url=https://api.github.com/users/lsarot/events{/privacy}, received_events_url=https://api.github.com/users/lsarot/received_events, type=User, site_admin=false, name=null, company=null, blog=, location=null, email=null, hireable=null, bio=Software Engineer, Java & Android development., public_repos=0, public_gists=0, followers=0, following=0, created_at=2020-04-24T13:41:18Z, updated_at=2020-04-24T18:28:30Z, private_gists=0, total_private_repos=0, owned_private_repos=0, disk_usage=0, collaborators=0, two_factor_authentication=false, plan={name=free, space=976562499, collaborators=0, private_repos=10000}}]
		return Collections.singletonMap("login", principal.getAttribute("login"));
    }

}
