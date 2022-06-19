package com.example.lasalle_ddm1_ex6_todolistv3.API;

public class ApiController {
    private static int todoAPICounter = 1;

    private Integer id;
    private String title;
    private Boolean completed;

    public ApiController(String title) {
        todoAPICounter++;
        this.id = todoAPICounter;
        this.title = title;
        this.completed = false;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean isCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

}
