package me.dlkanth.leadtodo

import me.dlkanth.leadtodo.asana.AsanaServices
import me.dlkanth.leadtodo.asana.TodoPojo
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by lakshmikanth on 1/28/2017.
 */
class TodoParserTask extends DefaultTask {

    def accessToken
    def workspaceName
    def projectName

    File sourceFile
    List<TodoPojo> todoList = new ArrayList<>()
    private static final Pattern todoPattern = Pattern.compile("//[\\t\\s]*TODO(.*)", Pattern.CASE_INSENSITIVE)

    @TaskAction
    def run() {

        if (accessToken == null || workspaceName == null || sourceFile == null) {
            throw new GradleException("LeadTodo accessToken and workspace name cannot be null")
        }

        sourceFile.eachFileRecurse {
            file ->
                def fName = file.getName()
                def fExt = null
                try {
                   fExt = fName.substring(fName.lastIndexOf('.'), fName.length())
                } catch (Exception e) {}

                if (fExt != null && (fExt.contains("java") || fExt.contains("groovy"))) {

                    BufferedReader reader = new BufferedReader(new FileReader(file))
                    Matcher matcher = null
                    def temp = reader.readLine()
                    def ln = 1

                    while (temp != null) {
                        matcher = todoPattern.matcher(temp)
                        if (matcher.find()) {
                            TodoPojo todo = new TodoPojo()
                            todo.taskName = matcher.group()
                            todo.taskNotes = "FileName: " + fName + "\nLineNumber: " + ln
                            todoList.add(todo)
                        }
                        temp = reader.readLine()
                        ln++
                    }

                    reader.close()
                }
        }

        if (todoList.size() > 0) {
            AsanaServices service = new AsanaServices()
            service.todoList = todoList
            service.accessToken = accessToken
            service.workSpaceName = workspaceName
            service.projectName = projectName
            service.startService()
        } else {
            System.out.println("No ToDo Comment found by LeadTodo")
        }

    }
}
