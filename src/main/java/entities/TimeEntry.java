package entities;

import jakarta.persistence.*;

@Entity
public class TimeEntry {
    @GeneratedValue
    @Id
    private Long id;

    public TimeEntry() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    private int hours;

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public TimeEntry(int hours) {
        this.hours = hours;
    }

    @ManyToOne
    private Project project;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
