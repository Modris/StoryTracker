# Story Tracker. Some features:

* Organize your TV-Shows, Movies, Books or Comic books
  
* Add multiple comments with date to a single story to capture the moment

* Column "Last Read days" track your story since you last read it
  
* Sort your data based on Columns in Ascending or Descending order such as ID, Name, Creation Date etc.

* Dynamic actions: Create, Read, Delete or Edit your stories. Filter columns. Filter page sizes. 

* Spring Security adds protection. No Cross-Site Request Forgery attempts can be made. Passwords are encrypted with BCrypt
  
# Small Demo link: Click image to be redirected to youtube.

[![Video Demo](/project_images/login_img.png)](https://www.youtube.com/watch?v=t1Xp0n6JElM)


# The tech stack. Main takeaways:

Spring Boot + Spring Data JPA + Spring Security + MySQL + Thymeleaf.

* Spring boot enables faster development and helps with dependency management. @Beans Reduces boilerplate code with dependecy injection and help with testing speed. 

* Connected to MySQL with Spring Data JPA repositories. This higher abstraction level helps with development, code management and testing because even the simplest JDBC requests take a chunk of code to write. Not to mention closing connections.

* MySQL database tables mapped to Java classes with Hibernate.

* Thymeleaf for dynamic view generation.

* Spring Security cross-site request forgery protection, request authentication, form login, session user and logout functionality.

*  Service Tests with TestContainers which containerize a real MySQL database so i can test functionality in isolation. No H2 or other in memory database testing.

# More project images:

![Default page](/project_images/firstGif.gif)


![Home Page Image](/project_images/homePage2_img.png)

![Edit Story Image](/project_images/editStory_img.png)

![Notes Page Image](/project_images/notesPage_img.png)

# Schema view.

![Notes Page Image](/project_images/schema.png)

# Set Up

1) Start by opening up MySQL workbench and creating a database. All table creation data is in schema.sql in src-> TEST -> resources folder -> schema.sql
2) Edit application.properties to connect to your MySQL database with your credentials.
3) Insert into Category and Status default data. The reason being that the 4 values were meant to be Enums. And the id values 1-4 would be constant. The idea was for future feature possibility of users adding their own categories or statuses.

   INSERT INTO categories VALUES (NULL,"Movies"), (NULL,"Books"),
                                 (NULL,"TV-Shows"),(NULL,"Comic books");

      INSERT INTO status VALUES(NULL,"Ongoing"),(NULL,"Hiatus"),
                        (NULL,"Completed"),(NULL,"Dropped");

4) Launch application on localhost:8080. Register in registration page.
5) Login. Add story tracker objects and test functionality.


# Why stop here?

* This was my first webpage with Spring framework, Spring Data JPA and Spring Security. I thought about a website that would be useful to me. I wanted to implement a lot more features but i felt learning how to consume external REST API's is more important to me right now. Imagine Story Tracker but it has access to a library of book names or a sea of movies you can choose from. That's how most popular websites are built. And that's how i'll build my next website.

