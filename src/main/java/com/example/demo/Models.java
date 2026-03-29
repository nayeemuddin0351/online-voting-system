package com.example.demo;

import java.util.*;

public class Models { } // Dummy class to keep the file name

class User {
    String id, name, password, role, nid;
    int age;
    boolean hasVoted = false;
    String votedFor = "";

    User(String id, String name, String password, String role, String nid, int age) {
        this.id = id; this.name = name; this.password = password;
        this.role = role; this.nid = nid; this.age = age;
    }
}

class Candidate extends User {
    String party, icon, status = "PENDING"; 
    int votes = 0;
    Candidate(User u, String party, String icon) {
        super(u.id, u.name, u.password, "CANDIDATE", u.nid, u.age);
        this.party = party; this.icon = icon;
    }
}