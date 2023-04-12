import entities.Project;
import entities.TimeEntry;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.Random;

public class Main {
    private static EntityManagerFactory entityManagerFactory;
    private static EntityManager entityManager;

    public static void main(String[] args) throws InterruptedException {
        Random rand = new Random();

        // Setup connection
        printAndPause("Connecting to DB...");
        entityManagerFactory = Persistence.createEntityManagerFactory("default");
        entityManager = entityManagerFactory.createEntityManager();

        // Create new project
        int estimateHours = rand.nextInt(200) + 1;
        var project1 = new Project("Some project name", estimateHours);
        printAndPause("Create project: " + project1);
        createProject(project1);

        // Create another new project
        estimateHours = rand.nextInt(200) + 1;
        var project2 = new Project("Some project name", estimateHours);
        printAndPause("Create project: " + project2);
        createProject(project2);

        // Get last project
        var projects = getProjects_QUERY();
        var lastProject = projects.get(projects.size()-1);

        // Update estimated hours
        int hoursToAdd = rand.nextInt(50) + 1;
        printAndPause("Add "+hoursToAdd+" to project: " + lastProject);
        increaseProjectHours(lastProject, hoursToAdd);

        // Get highest hours project and delete it
        var highestHoursProject = getProjectWithHighestEstimateHours();
        printAndPause("Delete project: " + highestHoursProject);
        deleteProject(highestHoursProject.getId());

        // Add a time entry to first project
        var project = projects.get(0);
        printAndPause("Add hours to project: " + project);
        project.getTimeEntries().add(new TimeEntry(5));
        project.getTimeEntries().add(new TimeEntry(15));
        updateProject(project);

        entityManager.close();
        entityManagerFactory.close();
    }




    private static List<Project> getProjects_QUERY() {
        var queryString = "SELECT p FROM Project p";
        var query = entityManager.createQuery(queryString, Project.class);
        return query.getResultList();
    }

    private static List<Project> getProjects_CRITERIA() {
        // Get the CriteriaBuilder instance from the entity manager
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Create a new CriteriaQuery instance and specify the type of the result as Project
        CriteriaQuery<Project> cq = cb.createQuery(Project.class);

        // Define the root entity for the query, which is the Project class
        Root<Project> rootEntry = cq.from(Project.class);

        // Specify that we want to select all projects
        CriteriaQuery<Project> all = cq.select(rootEntry);

        // Create a TypedQuery instance with the specified CriteriaQuery
        TypedQuery<Project> allQuery = entityManager.createQuery(all);

        // Execute the query and return the results as a list of Project objects
        return allQuery.getResultList();
    }


    private static Project getProjectById_QUERY(int id) {
        String queryString = "SELECT p FROM Project p WHERE p.id = :id";
        TypedQuery<Project> query = entityManager.createQuery(queryString, Project.class);

        query.setParameter("id", id);

        List<Project> results = query.getResultList();

        if (!results.isEmpty()) {
            return results.get(0);
        } else {
            return null;
        }
    }


    private static Project getProjectById_CRITERIA(Long id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Project> cq = cb.createQuery(Project.class);
        Root<Project> rootEntry = cq.from(Project.class);

        cq.where(cb.equal(rootEntry.get("id"), id));

        TypedQuery<Project> query = entityManager.createQuery(cq);

        return query.getResultList().stream().findFirst().orElse(null);
    }

    public static void createProject(Project project){
        var transaction = entityManager.getTransaction();

        transaction.begin();

        entityManager.persist(project);

        transaction.commit();
    }

    private static  void increaseProjectHours(Project project, int hoursToAdd) {

        // Start a transaction
        var transaction = entityManager.getTransaction();
        transaction.begin();

        // Merge the project with the persistence context
        Project mergedProject = entityManager.merge(project);

        // Update the project hours
        var updatedHours = mergedProject.getEstimateHours() + hoursToAdd;
        mergedProject.setEstimateHours(updatedHours);

        // Commit the transaction
        transaction.commit();
    }

    private static  void updateProject(Project project) {

        // Start a transaction
        var transaction = entityManager.getTransaction();
        transaction.begin();

        // Merge the project with the persistence context
        entityManager.merge(project);

        // Commit the transaction
        transaction.commit();
    }


    public static void deleteProject(Long id) {
        var transaction = entityManager.getTransaction();
        transaction.begin();
        Project project = entityManager.find(Project.class, id);

        if (project != null) {
            entityManager.remove(project);
        }

        transaction.commit();
    }


    public static Project getProjectWithHighestEstimateHours() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Project> cq = cb.createQuery(Project.class);
        Root<Project> root = cq.from(Project.class);

        cq.select(root).orderBy(cb.desc(root.get("estimateHours")));
        TypedQuery<Project> query = entityManager.createQuery(cq);
        query.setMaxResults(1);

        List<Project> result = query.getResultList();

        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }


    public static void printAndPause(String message) throws InterruptedException {
        System.out.println(message);
        Thread.sleep(500);
    }

}
