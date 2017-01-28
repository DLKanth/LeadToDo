package me.dlkanth.leadtodo

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project


/**
 * Created by lakshmikanth on 1/28/2017.
 */
class LeadTodo implements Plugin<Project> {

    void apply(Project project) {

        project.extensions.create("leadTodo", LeadTodoExtn)

        project.afterEvaluate{

            if (project.leadTodo.accessToken == null || project.leadTodo.workspaceName == null) {
                throw new GradleException("LeadTodo AccessToken and WorkspaceName cannot be null")
            }

            TodoParserTask parserTask = project.tasks.create("leadTodo", TodoParserTask)
            parserTask.accessToken = project.leadTodo.accessToken
            parserTask.workspaceName = project.leadTodo.workspaceName
            parserTask.sourceFile = project.rootDir
            parserTask.projectName = project.rootProject.name + " - LeadTodo"
        }

    }
}
