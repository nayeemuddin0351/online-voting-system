package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.*;

@Controller
public class ElectionController {
    private static Map<String, User> userDB = new HashMap<>();
    private static List<Candidate> candidateList = new ArrayList<>();
    private static String electionStatus = "ONGOING"; // UPCOMING, ONGOING, CLOSED

    static {
        userDB.put("admin", new User("admin", "Club President", "admin123", "ADMIN", "12345", 30));
    }

    @GetMapping("/")
    public String loginPage() { return "login"; }

    @GetMapping("/register")
    public String registerPage() { return "register"; }

    @PostMapping("/doRegister")
    public String doRegister(@RequestParam String id, @RequestParam String name, @RequestParam String password, 
                             @RequestParam String nid, @RequestParam int age, Model model) {
        if (userDB.containsKey(id)) {
            model.addAttribute("error", "PROXY ALERT: ID already registered!");
            return "register";
        }
        userDB.put(id, new User(id, name, password, "VOTER", nid, age));
        return "redirect:/?success=Registration successful! Please login.";
    }

    @PostMapping("/doLogin")
    public String doLogin(@RequestParam String id, @RequestParam String password, HttpSession session, Model model) {
        User user = userDB.get(id);
        if (user != null && user.password.equals(password)) {
            session.setAttribute("user", user);
            return "redirect:/dashboard";
        }
        model.addAttribute("error", "Invalid ID or Password!");
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/";
        
        model.addAttribute("user", user);
        model.addAttribute("candidates", candidateList);
        model.addAttribute("phase", electionStatus);
        return "dashboard";
    }

    @PostMapping("/applyCandidate")
    public String apply(@RequestParam String party, @RequestParam int age, 
                        @RequestParam(required = false) boolean noCase, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (age >= 25 && noCase) {
            Candidate c = new Candidate(user, party, "🛡️");
            candidateList.add(c);
            return "redirect:/dashboard?success=Application submitted for Admin approval!";
        }
        return "redirect:/dashboard?error=Criteria not met (Age 25+ and No Case required)!";
    }

    @PostMapping("/vote")
    public String vote(@RequestParam String cid, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user.hasVoted) return "redirect:/dashboard?error=PROXY ALERT: You already voted!";
        
        for (Candidate c : candidateList) {
            if (c.id.equals(cid) && c.status.equals("APPROVED")) {
                c.votes++;
                user.hasVoted = true;
                user.votedFor = c.name;
                break;
            }
        }
        return "redirect:/dashboard?success=Vote cast successfully!";
    }

    @PostMapping("/admin/status")
    public String setStatus(@RequestParam String status) {
        electionStatus = status;
        return "redirect:/dashboard";
    }

    @PostMapping("/admin/approve")
    public String approve(@RequestParam String cid) {
        for (Candidate c : candidateList) {
            if (c.id.equals(cid)) c.status = "APPROVED";
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}