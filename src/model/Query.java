package model;

public class Query {


    private String num;
    private  String title;
    private String description;


    public Query(String num, String title, String description) {
        this.num = num;
        this.title = title;
        this.description = description;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNum() {
        return num;
    }


    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }









}
