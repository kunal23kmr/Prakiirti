package com.example.prakiirti

class UserHelperClass {

    lateinit var name: String
    lateinit var username: String
    lateinit var email: String
    lateinit var admin: String
    private lateinit var password: String


    public
    constructor(
        name: String,
        username: String,
        email: String,
        password: String,
        admin:String="0",

        ) {
        this.name = name
        this.username = username
        this.email = email
        this.admin=admin
        this.password = password

    }

    constructor()
}