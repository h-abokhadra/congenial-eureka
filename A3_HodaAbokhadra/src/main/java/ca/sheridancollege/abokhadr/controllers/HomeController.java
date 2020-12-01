package ca.sheridancollege.abokhadr.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.sheridancollege.abokhadr.beans.Comment;
import ca.sheridancollege.abokhadr.beans.ForumThread;
import ca.sheridancollege.abokhadr.database.DatabaseAccess;

//Hi Simon! I'm using the HTTPS instead of HTTP. Thanks! ^^
@Controller
public class HomeController {

	@Autowired
	@Lazy
	private DatabaseAccess da;

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@GetMapping("/getThreadById/{threadId}")
	public String getThreadById(Model model, @ModelAttribute Comment comment, @PathVariable Long threadId) {

		ForumThread thread = da.getThreadListById(threadId).get(0);
		model.addAttribute("currentThread", thread);
		model.addAttribute("commentList", da.getCommentListByThreadId(threadId));

		return "/secure/view_thread";
	}

	@GetMapping("/secure")

	public String secureIndex(Authentication authentication, Model model) {

		String email = authentication.getName();
		List<String> roleList = new ArrayList<String>();
		for (GrantedAuthority ga : authentication.getAuthorities()) {
			roleList.add(ga.getAuthority());
		}
		model.addAttribute("email", email);
		model.addAttribute("roleList", roleList);
		model.addAttribute("threadList", da.getThreadList());

		return "/secure/index";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/permission-denied")
	public String permissionDenied() {
		return "/error/permission-denied";
	}

	@GetMapping("/register")
	public String getRegister() {
		return "register";
	}

	@PostMapping("/insertComment")
	public String insertComment(Model model, @ModelAttribute Comment comment) {
		System.out.println("print");
		da.insertComment(comment);
		model.addAttribute("commentList", da.getCommentListByThreadId(comment.getThreadId()));
		model.addAttribute("currentThread", da.getThreadListById(comment.getThreadId()).get(0));
//		model.addAttribute("currentThread", thread);
		return "/secure/view_thread";
	}

	@PostMapping("/register")
	public String postRegister(@RequestParam String username, @RequestParam String password) {
		da.addUser(username, password);
		Long userId = da.findUserAccount(username).getUserId();
		da.addRole(userId, Long.valueOf(2));
		return "redirect:/";
	}

}