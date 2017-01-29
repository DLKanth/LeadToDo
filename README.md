# LeadToDo
A Gradle Plugin to manage your code // Todo comments using Asana Integeration.

Plugin will automatically push your todo comments as tasks in asana workspace and also mark the task as completed if the todo comment has been removed from your code.

# Usage
Archives are available in maven central. To use it, add the following lines in build.gradle

```gradle
apply plugin: 'me.dlkanth.leadtodo'

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'me.dlkanth:leadtodo:1.0'
    }
}
```

# Managing your ToDo Comments Using [Asana]: https://asana.com/

Create a `Personal Access Token` from Developer App Management 

`(app.asana.com -> My Profile Settings -> Apps -> Manage Developer Apps -> Create a New Personal Access Token)`
 Copy the personal access token and add it in your gradle.properties or just paste it in your build.gradle
 
 And Also specify the name of your asana workspace in your build.gradle
 
 ## Add LeadTodo in your build.gradle
 ```gradle
 leadTodo {
    accessToken 'PERSONAL_ACCESS_TOKEN_FROM_ASANA'
    workspaceName 'NAME_OF_THE_WORKSPACE_IN_ASANA'
} 
 ```
 
 ## Run LeadTodo
 Run the `leadTodo` gradle task from terminal or gradle tools window
 ```gradle
 gradlew leadTodo
 ```
 
 That's All.
 

