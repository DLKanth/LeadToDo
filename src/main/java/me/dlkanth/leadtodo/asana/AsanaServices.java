package me.dlkanth.leadtodo.asana;

import com.asana.Client;
import com.asana.models.Project;
import com.asana.models.Task;
import com.asana.models.Workspace;
import org.gradle.api.GradleException;

import java.util.Arrays;
import java.util.List;

/**
 * Created by lakshmikanth on 1/28/2017.
 */
public class AsanaServices {

    public List<TodoPojo> todoList;
    public String accessToken;
    public String workSpaceName;
    public String projectName;

    // Asana
    Client client;
    Workspace asanaWorkspace;
    com.asana.models.Project asanaProject;
    List<Task> tasksFromProject;

    public void setTodoList(List<TodoPojo> todoList) {
        this.todoList = todoList;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setWorkSpaceName(String workSpaceName) {
        this.workSpaceName = workSpaceName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }


    private Client getClient(String accessToken) {
        if (client == null) {
            client = Client.accessToken(accessToken);
        }
        return client;
    }

    private Workspace getAsanaWorkspace() {
       if (asanaWorkspace == null) {
           for (Workspace workspace : getClient(accessToken).workspaces.findAll()) {
               if (workspace.name.equals(this.workSpaceName)) {
                   asanaWorkspace = workspace;
               }
           }
       }

       return asanaWorkspace;
    }

    private Project getAsanaProject() throws Exception {
        if (asanaProject == null) {
            List<Project> projects = getClient(accessToken).projects.findByWorkspace(getAsanaWorkspace().id).execute();
            for (Project project: projects) {
                if (project.name.equals(this.projectName)) {
                    this.asanaProject = project;
                }
            }

            if (asanaProject == null) {
                asanaProject = getClient(accessToken).projects.createInWorkspace(getAsanaWorkspace().id)
                        .data("name", this.projectName)
                        .execute();
            }
        }

        return asanaProject;
    }

    private List<Task> storeAllTasksFromProject() throws Exception {
        if (tasksFromProject == null) {
           tasksFromProject = getClient(accessToken).tasks
                    .findByProject(getAsanaProject().id)
                    .execute();
        }

        return tasksFromProject;
    }

    private void addTask (TodoPojo todo) throws Exception {
        getClient(accessToken).tasks.createInWorkspace(getAsanaWorkspace().id)
                .data("name", todo.taskName)
                .data("notes", todo.taskNotes)
                .data("projects", Arrays.asList(getAsanaProject().id))
                .execute();
    }

    private void updateTaskCompletion (Task task) throws Exception {
        getClient(accessToken).tasks.update(task.id)
                .data("completed","true")
                .execute();
    }

    private void checkAndAddTask() throws Exception {
        for (TodoPojo todo: todoList) {
            if (tasksFromProject == null || tasksFromProject.size() == 0) {
                addTask(todo);
                continue;
            }
            boolean alreadyExists = false;
            for (Task asanaTask : tasksFromProject) {
                if (asanaTask.name.equals(todo.taskName)) {
                    alreadyExists = true;
                }
            }
            if (!alreadyExists) {
                addTask(todo);
            }
        }
    }

    private void checkAndUpdateTask() throws Exception {
        for (Task asanaTask : tasksFromProject) {
            if (asanaTask.completed) {
                continue;
            }
            boolean doesExistLocally = false;
            for (TodoPojo todo: todoList) {
                if (todo.taskName.equals(asanaTask.name)) {
                    doesExistLocally = true;
                }
            }
            if (!doesExistLocally) {
                updateTaskCompletion(asanaTask);
            }
        }
    }

    public void startService() {

        if (accessToken == null || projectName == null || workSpaceName == null) {
            throw new GradleException("Failed to Sync Todo List \naccess token or workspace name cannot be null");
        }

        try {
            storeAllTasksFromProject();

            checkAndAddTask();
            checkAndUpdateTask();

            System.out.println("LeadTodo todo comments synced!");

        } catch (Exception e) {
            e.printStackTrace(System.out);
            throw new GradleException("Some error occured while syncing todo comments \n");
        }

    }

}
