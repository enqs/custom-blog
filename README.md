# "Custom blog" - blogging application.

> Simple blogging platform.
    
## Table of contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Setup](#setup)
* [Features](#features)
* [Status](#status)

## General info
I'm creating this application as a practical way of developing my programming skills.

My goal is to practice coding and learn new stuff while working on a semi real-live project. Content is Lorem ipsum, although I'm doing my best to keep the blog pleasing to the eye.

## Technologies
* Java 14
* Spring 2.3.2
    * Spring Boot
    * Spring Web
    * Spring Data
    * Spring Security
* JUnit 5
* Mockito
* Thymeleaf
* MySQL 8.0
* H2 Database - for a test scope

## Setup

### Requirements
* JDK 14 or later
* Configured MySQL
    * Empty database schema
    * User with privileges to edit this schema

### Installation
* Download code manually or by executing `git clone https://github.com/enqs/custom-blog.git`
* Go to `custom-blog\src\main\resources`
* Rename `templateApplication.properties` to `application.properties`
* Edit `application.properties` with a favorite text editor
    * Replace all `***` with suitable property values
* Return to the application root directory
* Build an executable jar `./mvnw package`
* Go to `custom-blog\target`
* Run application `java -jar custom-blog-0.0.1-SNAPSHOT.jar`
* Blog's website is accessible at localhost 127.0. 0.1 using port 80


## Features
### Ready features
* Articles: read / edit / delete 
* Users: read / edit / delete 
* User access rights: user, writer, admin
* Secure unauthorized actions based on user roles 
* Interactive administration panel

### To-do list
* Error handling and messages
* Display random article
* Pagination
* Filter articles
* Filter users
* Comments
* Tags
* Sidebar: Latest articles
* Sidebar: Latest comments
* Upload images to the server
* Super-Admin role
* Database backup and restore

## Status
Project status: _no longer being worked on_.


