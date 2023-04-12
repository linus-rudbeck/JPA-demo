package entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class Project {
    @Id
    @GeneratedValue
    private Long id;

    private String projectName;
    private int estimateHours;



    public Project() {
    }

    public Project(String projectName, int estimateHours) {
        this.projectName = projectName;
        this.estimateHours = estimateHours;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getEstimateHours() {
        return estimateHours;
    }

    public void setEstimateHours(int estimate) {
        this.estimateHours = estimate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", projectName='" + projectName + '\'' +
                ", estimateHours=" + estimateHours +
                '}';
    }

    @OneToMany
    private List<TimeEntry> timeEntries;

    public List<TimeEntry> getTimeEntries() {
        return timeEntries;
    }

    public void setTimeEntries(List<TimeEntry> timeEntries) {
        this.timeEntries = timeEntries;
    }
}