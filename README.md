# Database Management Systems Project: Airline Client Application

In this project, an implementation of a PostgreSQL Database. The project includes a set of dummy data, scripts to load the data in DB, and Java code to build the client application.
Specifically we implement the following functionality using the given schema:
- [x] Add Plane: Ask the user for details of a plane and add it to the DB
- [x] Add Pilot: Ask the user for details of a pilot and add it to the DB
- [x] Add Flight: Ask the user for details of a flight and add it to the DB
- [x] Add Technician: Ask user for details of a technician and add it to the DB
- [ ] Book Flight: Given a customer and flight that he/she wants to book, determine the status of the reservation (Waitlisted/Confirmed/Reserved) and add the reservation to the database with appropriate status.
- [x] List number of available seats for a given flight: Given a flight number and a departure date, find the number of available seats in the flight.
- [x] List total number of repairs per plane in descending order: Return the list of planes in decreasing order of number of repairs that have been made on the planes.
- [ ] List total number of repairs per year in ascending order:  Return the years with the number of repairs made in those years in ascending order of number of repairs per year.
- [ ] Find total number of passengers with a given status:  For a given flight and passenger status, return the number of passengers with the given status.

## Getting Started

These instructions will help you get a project of the project and up and running on your local machine for development or testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

To run this code you need PostgreSQL installed on your machine. For instructions on how to install Postgres please refer to this [link](https://www.postgresql.org/docs/9.3/static/tutorial-install.html)

### Installing

Once Postgres has been installed on your machine you are ready to run the program.

To intialize the database, navigate to the postgresql folder and do the following:
```
source startPostgreSQL.sh
source createPostgreDB.sh
```
To verify your server is running type
```
pg_ctl status
```

Next, navigate to the src folder to compile and run the program. Type the following:
```
./compile.sh
./run.sh
```

## Running the tests

Each selection code allows you to implement the respective action/query.

## Authors

* **Rick Boshae** - [Personal Website](https://rboshae.github.io/)
* **Jason Zellmer** - [Github Profile](https://github.com/jzell001)

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Dr. Chinya Ravishankar
* Mr. Payas Rajan
