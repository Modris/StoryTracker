# Story Tracker. Register to:

* Organize your TV-Shows, Movies or Books
  
* Add multiple comments with date to a single story to capture the moment

* Columns "Last Read days" track your story since you last read it. 
  
* Sort your data based on chosen variables such as id, name, creation date, last read.

* Create, Read, Delete or Edit your stories

* Spring Security adds protection. No Cross-Site Request Forgery attempts can be made. Passwords are encrypted with BCrypt.
  
# Small Demo link: Click image to be redirected to youtube.

[![Video Demo](/project_images/login_img.png)](https://www.youtube.com/watch?v=t1Xp0n6JElM)


# The tech stack
Spring Boot + Spring Data JPA + Spring Security + MySQL + Thymeleaf.

* Spring boot enables faster development and helps with dependency management. @Beans Reduces boilerplate code with dependecy injection and help with testing speed. 

* Connected to MySQL with Spring Data JPA repositories. This higher abstraction level helps with development, code management and testing.

* MySQL database tables mapped to Java classes with Hibernate.

* Thymeleaf for dynamic view generation.

* Spring Security cross-site request forgery protection, request authentication, form login, session user and logout functionality.

*  Service Tests with TestContainers which containerize a real MySQL database so i can test functionality in isolation. No H2 or other in memory database testing.

# More project images:

![Default page](/project_images/firstGif.gif)

![Home Page Image](/project_images/homePage2_img.png)

![Edit Story Image](/project_images/editStory_img.png)

![Notes Page Image](/project_images/notesPage_img.png)
