:toc:
:icons: font
:source-highlighter: prettify
:project_id: gs-crud-with-vaadin

This guide walks you through the process of building an application that uses a
https://vaadin.com[Vaadin-based UI] on a Spring Data JPA based backend.

== What You Will build

You will build a Vaadin UI for a simple JPA repository. What you will get is an
application with full CRUD (Create, Read, Update, and Delete) functionality and a
filtering example that uses a custom repository method.

You can follow either of two different paths:

* Starting from the `initial` project that is already in the project.
* Making a fresh start.

The differences are discussed later in this document.

== What You Need

:java_version: 1.8
include::https://raw.githubusercontent.com/spring-guides/getting-started-macros/main/prereq_editor_jdk_buildtools.adoc[]


include::https://raw.githubusercontent.com/spring-guides/getting-started-macros/main/how_to_complete_this_guide.adoc[]

[[scratch]]
== Starting with Spring Initializr

You can use this https://start.spring.io/#!type=maven-project&language=java&platformVersion=2.5.5&packaging=jar&jvmVersion=11&groupId=com.example&artifactId=crud-with-vaadin&name=crud-with-vaadin&description=Demo%20project%20for%20Spring%20Boot&packageName=com.example.crud-with-vaadin&dependencies=data-jpa,h2[pre-initialized project] and click Generate to download a ZIP file. This project is configured to fit the examples in this tutorial.

To manually initialize the project:

. Navigate to https://start.spring.io.
This service pulls in all the dependencies you need for an application and does most of the setup for you.
. Choose either Gradle or Maven and the language you want to use. This guide assumes that you chose Java.
. Click *Dependencies* and select *Spring Data JPA* and *H2 Database*.
. Click *Generate*.
. Download the resulting ZIP file, which is an archive of a web application that is configured with your choices.

NOTE: We add the Vaadin dependency later in the guide.

NOTE: If your IDE has the Spring Initializr integration, you can complete this process from your IDE.

NOTE: You can also fork the project from Github and open it in your IDE or other editor.

=== Manual Initialization (optional)

If you want to initialize the project manually rather than use the links shown earlier, follow the steps given below:

. Navigate to https://start.spring.io.
This service pulls in all the dependencies you need for an application and does most of the setup for you.
. Choose either Gradle or Maven and the language you want to use. This guide assumes that you chose Java.
. Click *Dependencies* and select *Spring Data JPA* and *H2 Database*.
. Click *Generate*.
. Download the resulting ZIP file, which is an archive of a web application that is configured with your choices.

NOTE: If your IDE has the Spring Initializr integration, you can complete this process from your IDE.

[[initial]]
== Create the Backend Services

This guide is a continuation from https://spring.io/guides/gs/accessing-data-jpa[Accessing Data with JPA].
The only differences are that the entity class has getters and setters and the custom
search method in the repository is a bit more graceful for end users. You need not read
that guide to walk through this one, but you can if you wish.

If you started with a fresh project, you need to add entity and repository objects. If you
started from the `initial` project, these object already exist.

The following listing (from `src/main/java/com/example/crudwithvaadin/Customer.java`)
defines the customer entity:

====
[source,java]
----
include::complete/src/main/java/com/example/crudwithvaadin/Customer.java[]
----
====

The following listing (from
`src/main/java/com/example/crudwithvaadin/CustomerRepository.java`) defines the customer
repository:

====
[source,java]
----
include::complete/src/main/java/com/example/crudwithvaadin/CustomerRepository.java[]
----
====

The following listing (from
`src/main/java/com/example/crudwithvaadin/CrudWithVaadinApplication.java`) shows the
application class, which creates some data for you:

====
[source,java]
----
include::complete/src/main/java/com/example/crudwithvaadin/CrudWithVaadinApplication.java[]
----
====

== Vaadin Dependencies

If you checked out the `initial` project, you have all necessary dependencies already set
up. However, the rest of this section describes how to add Vaadin support to a fresh
Spring project. Spring's Vaadin integration contains a Spring Boot starter dependency
collection, so you need add only the following Maven snippet (or a corresponding Gradle
configuration):

====
[source,xml,indent=0]
----
include::complete/pom.xml[tag=starter]
----
====

The example uses a newer version of Vaadin than the default one brought in by the starter
module. To use a newer version, define the Vaadin Bill of Materials (BOM) as follows:

====
[source,xml,indent=0]
----
include::complete/pom.xml[tag=bom]
----
====

TIP: By default, Gradle does not support BOMs, but there is a handy
https://plugins.gradle.org/plugin/io.spring.dependency-management[plugin for that]. Check out the
https://github.com/spring-guides/gs-crud-with-vaadin/blob/main/complete/build.gradle[`build.gradle` build file for an example of how to accomplish the same thing].

== Define the Main View class

The main view class (called `MainView` in this guide) is the entry point for Vaadin's UI
logic. In Spring Boot applications, you need only annotate it with `@Route` and it is
automatically picked up by Spring and shown at the root of your web application. You can
customize the URL where the view is shown by giving a parameter to the `@Route`
annotation. The following listing (from the `initial` project at
`src/main/java/com/example/crudwithvaadin/MainView.java`) shows a simple "`Hello, World`"
view:

====
[source,java]
----
include::initial/src/main/java/com/example/crudwithvaadin/MainView.java[]
----
====

== List Entities in a Data Grid

For a nice layout, you can use the `Grid` component. You can pass the list of entities
from a constructor-injected `CustomerRepository` to the `Grid` by using the `setItems`
method. The body of your `MainView` would then be as follows:

====
[source,java]
----
@Route
public class MainView extends VerticalLayout {

	private final CustomerRepository repo;
	final Grid<Customer> grid;

	public MainView(CustomerRepository repo) {
		this.repo = repo;
		this.grid = new Grid<>(Customer.class);
		add(grid);
		listCustomers();
	}

	private void listCustomers() {
		grid.setItems(repo.findAll());
	}

}
----
====

TIP: If you have large tables or lots of concurrent users, you most likely do not want
to bind the whole dataset to your UI components.
+
Although Vaadin Grid lazy loads the data from the server to the browser, the preceding
approach keeps the whole list of data in the server memory. To save some memory, you could
show only the topmost results by employing paging or providing a lazy loading data
provider by using the `setDataProvider(DataProvider)` method.

== Filtering the Data

Before the large data set becomes a problem to your server, it is likely to cause a
headache for your users as they try to find the relevant row to edit. You can use a
`TextField` component to create a filter entry. To do so, first modify the
`listCustomer()` method to support filtering. The following example (from the `complete`
project in `src/main/java/com/example/crudwithvaadin/MainView.java`) shows how to do so:

====
[source,java,indent=0]
----
include::complete/src/main/java/com/example/crudwithvaadin/MainView.java[tag=listCustomers]
----
====

NOTE: This is where Spring Data's declarative queries come in handy. Writing
`findByLastNameStartsWithIgnoringCase` is a single line definition in the
`CustomerRepository` interface.

You can hook a listener to the `TextField` component and plug its value into that filter
method. The `ValueChangeListener` is called automatically as a user types because you
define the `ValueChangeMode.EAGER` on the filter text field. The following example shows
how to set up such a listener:

====
[source,java]
----
TextField filter = new TextField();
filter.setPlaceholder("Filter by last name");
filter.setValueChangeMode(ValueChangeMode.EAGER);
filter.addValueChangeListener(e -> listCustomers(e.getValue()));
add(filter, grid);
----
====

== Define the Editor Component

As Vaadin UIs are plain Java code, you can write re-usable code from the beginning. To do
so, define an editor component for your `Customer` entity. You can make it be a
Spring-managed bean so that you can directly inject the `CustomerRepository` into the
editor and tackle the Create, Update, and Delete parts or your CRUD functionality. The
following example (from `src/main/java/com/example/crudwithvaadin/CustomerEditor.java`)
shows how to do so:

====
[source,java]
----
include::complete/src/main/java/com/example/crudwithvaadin/CustomerEditor.java[]
----
====

In a larger application, you could then use this editor component in multiple places. Also
note that, in large applications, you might want to apply some common patterns (such as
MVP) to structure your UI code.

== Wire the Editor

In the previous steps, you have already seen some basics of component-based programming.
By using a `Button` and adding a selection listener to `Grid`, you can fully integrate
your editor into the main view. The following listing (from
`src/main/java/com/example/crudwithvaadin/MainView.java`) shows the final version of the
`MainView` class:

====
[source,java]
----
include::complete/src/main/java/com/example/crudwithvaadin/MainView.java[]
----
====

== Summary

Congratulations! You have written a full-featured CRUD UI application by using Spring Data
JPA for persistence. And you did it without exposing any REST services or having to write
a single line of JavaScript or HTML.

== See Also

The following guides may also be helpful:

* https://spring.io/guides/gs/spring-boot/[Building an Application with Spring Boot]
* https://spring.io/guides/gs/accessing-data-jpa/[Accessing Data with JPA]
* https://spring.io/guides/gs/accessing-data-mongodb/[Accessing Data with MongoDB]
* https://spring.io/guides/gs/accessing-data-gemfire/[Accessing Data with GemFire]
* https://spring.io/guides/gs/accessing-data-neo4j/[Accessing Data with Neo4j]
* https://spring.io/guides/gs/accessing-data-mysql/[Accessing data with MySQL]

include::https://raw.githubusercontent.com/spring-guides/getting-started-macros/main/footer.adoc[]
