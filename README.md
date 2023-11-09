# Angular-Spring-HATEOAS Integration

Welcome to this new repository showcasing the integration of Angular with Spring using the HATEOAS principle.

## Dependencies / Features

- **Spring Data JDBC**: Ideal for persisting DDD-style aggregates
- **Spring Rest Repositories**: Publishes a HAL REST API
- Integration test demonstrates testing the presence and absence of links.
- **Lombok**: Reduces boilerplate code
- Utilizes `frontend-maven-plugin` and Spring MVC configuration to build and serve the Angular app within Spring Boot
- **Spring Boot DevTools**: Facilitates auto-reload of the backend on save
- **Angular 12** for the graphical user interface (GUI)

## Business Case

In this scenario, a product manager submits a production order to the system ("Operation submit"). If not submitted yet, the production order can be renamed. Subsequently, a manufacturer accepts the production order ("Operation accept"), specifying the expected delivery date, which must be in the future.

All three operations are represented as HAL links on the production orders within the REST API. The GUI utilizes these links to render or hide the button that triggers the respective action.

### Benefits for the Frontend

- UI agnostic of URLs, relying solely on link relations
- A "dumb" engine, displaying whatever is enabled by the backend ("feature toggle")
- The self-link replaces the need for an aggregate ID

### Benefits for the Backend

- Business logic is entirely encapsulated in the backend
- Complete control over state transitions directly within the aggregate
- Business capabilities are expressed in the API

## How to Run

Execute the following command in the terminal:

```bash
./mvnw clean spring-boot:run
```
- Open your web browser and enter http://localhost:8080 to view the GUI.
- Access http://localhost:8080/api to explore the API's capabilities. Follow the links to various resources and actions.
