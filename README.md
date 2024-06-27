# Algorithms III Project - Year 2023

## Team Members:

- Theo Lijs (Student ID: 109472)
- Juan Martin De La Cruz (Student ID: 109588)
- Santiago Fassio (Student ID: 109463)

---

### Overview

Welcome to our Algorithms III project repository for the year 2023 at the Faculty of Engineering, University of Buenos Aires! This project focuses on learning and applying advanced Object-Oriented Programming (OOP) practices and utilizing various design patterns in implementing complex algorithms.

### Project Description

This repository serves as a platform to explore advanced algorithms through the lens of OOP principles and extensive use of design patterns. We aim to design robust, efficient, and maintainable solutions for complex computational problems. Each algorithm implementation is crafted with careful consideration of OOP concepts such as encapsulation, inheritance, polymorphism, and abstraction, alongside the application of design patterns including Factory, Singleton, Strategy, and Observer, among others.

### Repository Structure

- **Documentation:** Contains detailed documentation, including project reports, analysis findings, and theoretical background.

- **Implementations:** Source code for algorithms implemented using OOP principles and design patterns in Java.

- **Tests:** Input and output data used for testing algorithms, along with scripts for generating and processing data.

### General Design

In the framework of this project, we have implemented an interactive calendar using the Java programming language and the JavaFX graphical user interface (GUI) library. The calendar application was designed with an intuitive interface, providing a seamless user experience. Users can view daily, monthly, or weekly calendars, with the ability to navigate between different months and years. Additionally, features include viewing scheduled events, adding and deleting events, and setting reminders.

To achieve this, we utilized object-oriented programming concepts in Java to create a coherent data structure. We implemented appropriate classes and methods to handle calendar logic, such as date calculations, event management, and data saving and loading operations for persistence. Moreover, we applied various design principles such as TDA, DRY, POLC, POLA, POLK, FIRST, all SOLID principles, and as per the project's requirement SOC.

### Business Model

- **Calendar:** Class containing different types of events stored in their respective lists, including both Events and Tasks. It provides methods for adding, modifying, or deleting each type of event.

- **Event and Task:** Initially, we didn't realize that we could implement a generic interface for Events and Tasks. After noting commonalities between these types of events, we decided to create this interface. Each event has its title, description, a specific date, a list of associated alarms, and an option to denote that it can last all day.

- **Task:** Extends the Event class and adds the "completed" attribute to indicate if a task has been completed. The class constructor initializes inherited attributes and sets the initial state of the task as incomplete. It also creates a list to store alarms associated with that task.

- **Event:** Extends the Event class and adds the "endDate" attribute to indicate the date and time of the event's end. The class constructor initializes inherited attributes and sets the "endDate" attribute with the provided value. It also creates a list to store alarms associated with the event.

- **Recurring Event:** Subclass of the Event class representing an event that repeats over time. This class uses the Observer design pattern and implements the ObserverChildModified and ObserverChildDeleted interfaces. In addition to the inherited attributes of the Event class, Recurring Event has additional attributes such as frequency (defined by a Frequency object), days of the week (SortedSet indicating the days of the week the event repeats, valid only for weekly frequency), interval (indicates the repetition interval between each event), endDateRepetitions (LocalDateTime object indicating the date and time of the end of event repetitions), creationDate (represents the date and time of event creation), infinite (boolean indicating if the event has infinite repetitions or not), and repetitionsCount (indicates the total number of event repetitions).

- **Child Event:** Extends the Recurring Event class. It adds additional attributes to establish communication with the parent Recurring Event and defines methods to notify changes and deletions to the corresponding observer.

- **Saver:** Handles the serialization of all events in the current calendar along with their alarms in XML format. Saver creates three files in the file system used to store three types of objects (Tasks.xml, Events.xml, and Recurring Events.xml). It is worth mentioning that Composite was used as a design pattern to generate these XML files. Saver then creates three roots and orders all elements of the same type to serialize themselves, along with their alarms, into their respective root. Finally, Saver places the complete roots in the new documents created.

- **Reader:** Responsible for taking files created by Saver and deserializing the Events found in each with the aim of restoring the calendar saved in XML files.

### View

Our FXML files coordinate how the interface appears. They are responsible for the presentations and visualizations of data to the end user. Their main function is to display model information in a comprehensible form and facilitate interaction with the user. These FXML files are used to create the main scene and views for event and alarm creators.

### Controller

The Controller decides how buttons work and how data is displayed. It updates data through user event handlers and configuration setters, and then sets it in the calendar. Additionally, it initializes different views and retrieves data through getters to update the model. In summary, each controller initializes its respective views and modifies the model.