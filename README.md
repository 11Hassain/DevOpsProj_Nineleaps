## Project Access Management

### Description
>Project Access Management is a web platform designed for the Nineleaps DevOps team to manage access for various project-specific tools. This platform will streamline the process of providing and revoking access for different projects, which is currently a tedious job for the DevOps team. The platform will allow the DevOps team to manage access across projects efficiently.Project Access Management application will be responsible for managing access to various project specific tools. It will allow for authentication and authorization of users, as well as managing their access to various third party applications like GitHub.Project Access Management application will also allow admins to add, remove, and manage users, projects, and their associated tools.This README Document outlines the functionalities, dependencies and tech stacks used for this project.

### Roles
> Super Admin  
> Admin  
> PM  
> User  

### Tech stack
> IntelliJ IDEA version = 2023.1.1  
> MySQL = 5.7.41  
> Java version = 17  

### Installation 
>-To install the Project Access Management application, you'll need to follow these steps:  
>
>-Clone the repository from GitHub to your local machine.   
>
>-Navigate to the directory where the repository was cloned.   
>
>-Open the IDE and import the project into your IDE.  
>
>-Once all the files are imported, now open ProjectAccessManagementApplication file and run the code.  
>
>-Open http://localhost:8080/{endpoints} in postman, you can see application working.  

### Login
>To access the "Project Access Management" application, users will log in using Single Sign-On (SSO) with their valid email. If they don't have an account, they will need to contact their administrator to create one for them.


### Dependencies
> spring-boot-starter-test  
> spring-boot-starter-web  
> spring-boot-starter-jdbc   
> spring-boot-starter-data-jpa  
> lombok  
> spring-boot-maven-plugin   
> spring-boot-starter-validation  
> spring-security-web   
> spring-boot-starter-web-services   
> jackson-databind = 2.13.1   
> github-api = 1.314   
> jwt-jackson = 0.11.2   
> io.jsonwebtoken = 0.11.2   
> jjwt-api = 0.11.2   
> modelmapper = 2.4.4   
> hibernate-core = 6.2.0.CR4   
> mysql-connector-java = 8.0.32    
