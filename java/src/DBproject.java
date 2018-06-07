/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class DBproject{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	public DBproject(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");

			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}catch(Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}

	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 *
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate (String sql) throws SQLException {
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
	    stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 *
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;

		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 *
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 * obtains the metadata object for the returned result set.  The metadata
		 * contains row and column info.
		*/
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;

		//iterates through the result set and saves the data returned by the query.
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>();
		while (rs.next()){
			List<String> record = new ArrayList<String>();
			for (int i=1; i<=numCol; ++i)
				record.add(rs.getString (i));
			result.add(record);
		}//end while
		stmt.close ();
		return result;
	}//end executeQueryAndReturnResult

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 *
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		int rowCount = 0;

		//iterates through the result set and count nuber of results.
		if(rs.next()){
			rowCount++;
		}//end while
		stmt.close ();
		return rowCount;
	}

	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current
	 * value of sequence used for autogenerated keys
	 *
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */

	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();

		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
	         // ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 *
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
				"Usage: " + "java [-classpath <classpath>] " + DBproject.class.getName () +
		            " <dbname> <port> <user>");
			return;
		}//end if

		DBproject esql = null;

		try{
			System.out.println("(1)");

			try {
				Class.forName("org.postgresql.Driver");
			}catch(Exception e){

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}

			System.out.println("(2)");
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];

			esql = new DBproject (dbname, dbport, user, "");

			boolean keepon = true;
			while(keepon){
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. Add Plane");
				System.out.println("2. Add Pilot");
				System.out.println("3. Add Flight");
				System.out.println("4. Add Technician");
				System.out.println("5. Book Flight");
				System.out.println("6. List number of available seats for a given flight.");
				System.out.println("7. List total number of repairs per plane in descending order");
				System.out.println("8. List total number of repairs per year in ascending order");
				System.out.println("9. Find total number of passengers with a given status");
				System.out.println("10. < EXIT");

				switch (readChoice()){
					case 1: AddPlane(esql); break;
					case 2: AddPilot(esql); break;
					case 3: AddFlight(esql); break;
					case 4: AddTechnician(esql); break;
					case 5: BookFlight(esql); break;
					case 6: ListNumberOfAvailableSeats(esql); break;
					case 7: ListsTotalNumberOfRepairsPerPlane(esql); break;
					case 8: ListTotalNumberOfRepairsPerYear(esql); break;
					case 9: FindPassengersCountWithStatus(esql); break;
					case 10: keepon = false; break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if
			}catch(Exception e){
				// ignored.
			}
		}
	}

	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}//end readChoice


    //////////////////////////////////////////////////////////////////////////////////
	public static void AddPlane(DBproject esql) {//1

		// To add a plane we need to collect the following information: id, make, model, age, seats.
		// id is generated by the DBproject.java file.

		try{

			String make = "", model = "", age = "", seats="";
			//=============//
			// User Prompt //
			//=============//

				// Prompt user for make
				System.out.print("Enter make: ");
				make = in.readLine();

				// Prompt user for model
				System.out.print("Enter model: ");
				model = in.readLine();

				// Prompt user for age
				System.out.print("Enter age: ");
				age = in.readLine();

				// Prompt user for seats
				System.out.print("Enter number of seats: ");
				seats = in.readLine();

				System.out.println("Make: " + make + ", Model: " + model + ", Age: " + age + ", Seats: " + seats);

				// System.out.println("Confirm Add Plane(y/n)?");
				// String answer = in.readLine();
				//
				// if (answer.equals("n") || answer.equals("no")) {
				// 	 return; // Do not add plane. Return to menu.
				//  }

			//===============//
			// Insert Plane  //
			//===============//

			// maxIDQuery is the current max primary id value
			int maxIDQuery = Integer.valueOf(esql.executeQueryAndReturnResult("SELECT max(id) FROM plane;").get(0).get(0));

			maxIDQuery++;	// increment maxIDQuery to get next primary key for new plane.

      String query = "INSERT INTO plane VALUES (" + maxIDQuery
									 + ", \'" + make + "\', \'" +  model + "\', "
									 + age + ", " + seats + ");";	// prepare insert statement
      esql.executeUpdate(query); 								//insert new plane into database

			System.out.println("Plane added to database.");

    }catch(Exception e){
       System.err.println (e.getMessage());
    }
	}

	public static void AddPilot(DBproject esql) {//2

	try{

         // my code

         int getID = Integer.valueOf(esql.executeQueryAndReturnResult("SELECT max(id) FROM Pilot;").get(0).get(0));
         getID++;

		 //System.out.println("current ID: " + getID); // debugging output

         System.out.print("Enter fullname: ");
         String fullname = in.readLine();

         //System.out.println("fullname: " + fullname); // debugging output

         System.out.print("Enter nationality: ");
         String nationality = in.readLine();

         //System.out.println("nationality: " + nationality); // debugging output

         String query = "INSERT INTO Pilot VALUES(" + getID + ", \'" + fullname + "\', \'" + nationality + "\');";

         esql.executeUpdate(query);

         System.out.println("Pilot added to database.");

	}catch(Exception e){
         System.err.println (e.getMessage());
    }

	}

	public static void AddFlight(DBproject esql) {//3
		// Given a pilot, plane and flight, adds a flight in the DB

		try{

			int cost = 0, num_sold = 0, num_stops = 0;

			String departure_date = "", arrival_date = "", arrival_airport = "", departure_airport="", departure_time="", arrival_time="";

			int pilotID = -1, planeID = -1;

			//=============//
			// User Prompt //
			//=============//

			// Prompt user for flight cost
			System.out.print("Enter flight cost (Whole Number Only): ");
			cost = Integer.valueOf(in.readLine());
			while (cost < 0) {
				System.out.print("The flight cost should be greater than 0. \nPlease try again.\n");
				System.out.println("Enter flight cost (Whole Number Only): ");
				cost = Integer.valueOf(in.readLine());
			}

			// Prompt user for number of tickets sold
			System.out.print("Enter number of tickets sold: ");
			num_sold = Integer.valueOf(in.readLine());
			while (num_sold < 0) {
				System.out.print("The number of tickets sold should be greater than 0. \nPlease try again.\n");
				System.out.print("Enter number of tickets sold: ");
				num_sold = Integer.valueOf(in.readLine());
			}

			// Prompt user for number of stops
			System.out.print("Enter number of stops: ");
			num_stops = Integer.valueOf(in.readLine());
			while (num_stops < 0) {
				System.out.print("The number of stops should 0 or more. \nPlease try again.\n");
				System.out.print("Enter number of stops: ");
				num_stops = Integer.valueOf(in.readLine());
			}

			// Prompt user for departure date
			System.out.print("Enter Departure Date (YYYY-MM-DD): ");
			departure_date = in.readLine();
			while (!(validDate(departure_date))){
				System.out.print("Invalid Date. Please Re-Enter Departure Date (YYYY-MM-DD): ");
				departure_date = in.readLine();
			}

			// Prompt user for departure time
			System.out.print("Enter Departure Time (HH:MM): ");
			departure_time = in.readLine();


			// Prompt user for arrival date
			System.out.print("Enter Arrival Date (YYYY-MM-DD): ");
			arrival_date = in.readLine();
			while (!(validDate(arrival_date))){
				System.out.print("Invalid Date. Please Re-Enter Departure Date (YYYY-MM-DD): ");
				arrival_date = in.readLine();
			}

			// Prompt user for arrival time
			System.out.print("Enter Arrival Time (HH:MM): ");
			arrival_time = in.readLine();


			// Prompt user for departure airport
			System.out.print("Enter Departure Airport (Five characters or less): ");
			departure_airport = in.readLine();
			while (departure_airport.length() > 5) {
				String departure_airport_substring = departure_airport.substring(0, 5);

				System.out.println(departure_airport + " is too long. Press enter to automatically shorten to " + departure_airport_substring  + " or re-enter departure_airport.");

				System.out.print("Enter Departure Airport (Five characters or less): ");
				String reentered_departure_airport = in.readLine();

				if (reentered_departure_airport.equals("")) {
					departure_airport = departure_airport_substring;
				} else {
					departure_airport = reentered_departure_airport;
				}

			}

			// Prompt user for arrival airport
			System.out.print("Enter Arrival Airport (Five characters or less): ");
			arrival_airport = in.readLine();

			while (arrival_airport.length() > 5) {
				String arrival_airport_substring = arrival_airport.substring(0, 5);

				System.out.println(arrival_airport + " is too long. Press enter to automatically shorten to " + arrival_airport_substring  + " or re-enter arrival_airport.");

				System.out.print("Enter Arrival Airport (Five characters or less): ");
				String reentered_arrival_airport = in.readLine();

				if (reentered_arrival_airport.equals("")) {
					arrival_airport = arrival_airport_substring;
				} else {
					arrival_airport=reentered_arrival_airport;
				}

			}

			// Prompt user for pilot_id HERE
			System.out.print("Enter Pilot ID: ");
			pilotID = Integer.valueOf(in.readLine());

			// Check is pilot_id is valid
			int pilotExists = esql.executeQuery("SELECT * FROM Pilot Where id="+pilotID+";");

			while (pilotExists < 1) {
				System.out.println("Invalid Pilot ID. Please try again or enter q to return to menu.");
				System.out.print("Enter Pilot ID: ");
				String userInput = in.readLine();
				if (userInput.equals("q")) {
					return;
				}
				pilotID = Integer.valueOf(userInput);
				pilotExists = esql.executeQuery("SELECT * FROM Pilot Where id="+pilotID+";");
			}


			// Prompt user for plane_id
			System.out.print("Enter Plane ID: ");
			planeID = Integer.valueOf(in.readLine());

			// Check is plane_id is valid
			int planeExists = esql.executeQuery("SELECT * FROM Plane Where id="+planeID+";");

			while (planeExists < 1) {
				System.out.println("Invalid Plane ID. Please try again or enter q to return to menu.");
				System.out.print("Enter Plane ID: ");
				String userInput = in.readLine();
				if (userInput.equals("q")) {
					return;
				}
				planeID = Integer.valueOf(userInput);
				planeExists = esql.executeQuery("SELECT * FROM Plane Where id="+planeID+";");
			}






			// System.out.println("Cost: " + cost + ", Tickets Sold: " + num_sold + ", Departure Date: " + departure_date + ", Arrival Date: " + arrival_date + ", Arrival Airport: " + arrival_airport + ", Departure Airport: " + departure_airport);

			// System.out.println("Add Flight(y/n)?");
			// String answer = in.readLine();
			//
			// if (answer.equals("n") || answer.equals("no")) {
			// 	 return;
			//  }



			//===============//
			// Insert Flight //
			//===============//

			// maxIDQuery is the current max primary id value in the flight table plus 1
			int flightID = Integer.valueOf(esql.executeQueryAndReturnResult("SELECT max(fnum) FROM flight;").get(0).get(0)) + 1;

      String insertFlightStatement = "INSERT INTO flight VALUES (" + flightID
									 + ", " + cost + ", " +  num_sold + ", "
									 + num_stops + ", \'" + departure_date + "\', \'" + arrival_date + "\', \'" + arrival_airport + "\', \'" + departure_airport + "\');";	// prepare insert flight statement

      esql.executeUpdate(insertFlightStatement); 								//insert new flight into database

			System.out.println("Flight added to database.");

			//===================//
			// Insert FlightInfo //
			//===================//
			// Parameters for FlightInfo(fiid, flight_id, pilot_id, plane_id, technician_id)

			// flightInfoID is the current max primary id value in the flight table plus 1.
			int flightInfoID = Integer.valueOf(esql.executeQueryAndReturnResult("SELECT max(fnum) FROM flight;").get(0).get(0)) +1;

			// Prepare FlightInfo insert statement.
			String insertFlightInfoStatement = "INSERT INTO FlightInfo VALUES (" + flightInfoID + ", " +flightID+", "+pilotID+", "+planeID+");";

			esql.executeUpdate(insertFlightInfoStatement);
			System.out.println("FlightInfo added to database.");

			//===================//
			// Insert Schedule   //
			//===================//
			// Parameters for Schedule(id, flightNum, departure_time, arrival_time)

			// scheduleID is the result of adding one to max primary id value in the schedule table
			int scheduleID = Integer.valueOf(esql.executeQueryAndReturnResult("SELECT max(fnum) FROM flight;").get(0).get(0)) + 1;

			String insertScheduleStatement = "INSERT INTO Schedule VALUES ("+scheduleID+", "+flightID+", \'"+departure_date+" "+departure_time+"\', \'"+arrival_date+" "+arrival_time+"\');";
			// System.out.println(insertScheduleStatement); // Debugging
			esql.executeUpdate(insertScheduleStatement);
			System.out.println("Schedule added to database.");

    }catch(Exception e){
    	System.err.println (e.getMessage());
    }

	}

	public static void AddTechnician(DBproject esql) {//4


       try{
          // my code

         int getID = Integer.valueOf(esql.executeQueryAndReturnResult("SELECT max(id) FROM Technician;").get(0).get(0));
         getID++;

		 //System.out.println("current ID: " + getID); // debugging output

         System.out.print("Enter fullname: ");
         String fullname = in.readLine();

         // System.out.println("fullname: " + fullname); // debugging output

         String query = "INSERT INTO Technician VALUES(" + getID + ", \'" + fullname + "\');";

         esql.executeUpdate(query);

         System.out.println("Technician added to database.");

       }catch(Exception e){
         System.err.println (e.getMessage());
       }

	}

	public static void BookFlight(DBproject esql) {//5
		// Given a customer and a flight that he/she wants to book, add a reservation to the DB
		// Book Flight: Given a customer and flight that he/she wants to book, determine the status of the
		//							reservation (Waitlisted/Confirmed/Reserved) and add the reservation to the database with appropriate status.
		try{
			String reservation_status = ""; // Used for Insert on Reservation table

			// Get customer id
			System.out.println("Please enter the Customer ID");
			int customerID=Integer.valueOf(in.readLine());
			// List<List<String>> query_result = esql.executeQueryAndReturnResult("SELECT fname, lname, gtype FROM customer WHERE id=" +customerID+";");

			// Get flight id
			System.out.println("Please enter the Flight Number to book: ");
			int user_provided_fnum = Integer.valueOf(in.readLine());

			// Get number of seats sold from flight
			int seats_sold = Integer.valueOf(esql.executeQueryAndReturnResult("Select F.num_sold FROM Flight F WHERE F.fnum="+ user_provided_fnum +";").get(0).get(0));
			System.out.println("Number of seats sold: " + seats_sold); // Debugging

			//Get number of seats available on the plane.
			int seats_total = Integer.valueOf(espql.executeQueryAndReturnResult("SELECT P.seats FROM FlightInfo FI, Plane P WHERE FI.flight_id=" + user_provided_fnum + " AND FI.plane_id=P.id;"));
			System.out.println("Number of seats on plane: " + seats_sold); // Debugging

			// Compare number of seats sold from Flight table with number of seats available on plane from plane table.
			int seats_available = seats_total - seats_sold;
			System.out.println("There are " + seats_available + " seats available.");

			if(seats_available > 0) {
				reservation_status = "R";
			} else {
				reservation_status = "W";
			}

			// Prepare to add reservation to the database with appropriate status
			// Update number of seats sold in flight table.
			espql.executeUpdate("UPDATE Flight SET num_sold =num_sold+1"); // HERE
			// Insert reservation to reservation table
			espql.executeUpdate("INSERT INTO Reservation VALUES " + rnum + ", " + customerID + ", " + user_provided_fnum + ", " + reservation_status + ";");
			System.out.println("Customer Added to Flight");


		}catch(Exception e){

		}
	}

	public static void ListNumberOfAvailableSeats(DBproject esql) {//6
		// For flight number and date, find the number of availalbe seats (i.e. total plane capacity minus booked seats )

        // Given a flight number and a departure date, find the number of available seats in a flight.

        // Flight(F):
        //    - fnum
        //    - cost
        //    - num_sold *
        //    - num_stops
        //    - actual_departure_date *
        //    - actual_arrival_date
        //    - arrival_airport
        //    - dpearture_airport
        //
        // Plane(P):
        //    - id *
        //    - make
        //    - model
        //    - age
        //    - seats *
        //
        // FlightInfo(FI):
        //    - fiid
        //    - flight_id *
        //    - pilot_id
        //    - plane_id *

        try{
          // my code

            // Get flight id
			System.out.print("Please enter the Flight Number: ");
			int user_provided_fnum = Integer.valueOf(in.readLine());

            System.out.print("Please enter a departure date and time (i.e., 2014-05-01 16:45): ");
            String user_provided_date_time = in.readLine();


            ////////////////////////////////////////////////////////////////////////
			// Get number of seats sold from flight
			int seats_sold = Integer.valueOf(esql.executeQueryAndReturnResult("SELECT F.num_sold FROM Flight F WHERE F.fnum="+ user_provided_fnum + " AND F.actual_departure_date=\'"+ user_provided_date_time + "\';").get(0).get(0));

            System.out.println("Number of seats sold: " + seats_sold); // Debugging


            ////////////////////////////////////////////////////////////////////////
			//Get number of seats available on the plane.
			int seats_total = Integer.valueOf(esql.executeQueryAndReturnResult("SELECT P.seats FROM FlightInfo FI, Plane P WHERE FI.flight_id=" + user_provided_fnum + " AND FI.plane_id=P.id;").get(0).get(0));

            System.out.println("Number of seats on plane: " + seats_total); // Debugging


            ////////////////////////////////////////////////////////////////////////
			// Compare number of seats sold from Flight table with number of seats available on plane from plane table.
			int seats_available = seats_total - seats_sold;

            System.out.println("There are " + seats_available + " seats available.");



        }catch(Exception e){
         System.err.println (e.getMessage());
       }




	}

	public static void ListsTotalNumberOfRepairsPerPlane(DBproject esql) {//7
		// Count number of repairs per planes and list them in descending order
        
        // List total number of repairs per plane in descending order
        // Return the list of planes in descreasing order of number of repairs that have been made on the planes
        
        try{
          // my code
          
          String query = "SELECT R.plane_id, COUNT(*) FROM repairs R GROUP BY R.plane_id ORDER BY count DESC;";

          esql.executeQueryAndPrintResult(query);
         
          System.out.println("List Total Number of Repairs completed.");
          
          
          
          
        }catch(Exception e){
         System.err.println (e.getMessage());
       }  
        
	}

	public static void ListTotalNumberOfRepairsPerYear(DBproject esql) {//8
		// Count repairs per year and list them in ascending order
	}

	public static void FindPassengersCountWithStatus(DBproject esql) {//9
		// Find how many passengers there are with a status (i.e. W,C,R) and list that number.
	}

	// Added Functions
	public static boolean validDate(String date) {
		if (date.length()!=10) return false;

		// Parse date, expected input YYYY-MM-DD
		String[] parsedDate = date.split("-");
		int year = Integer.valueOf(parsedDate[0]);
		int month = Integer.valueOf(parsedDate[1]);
		int day = Integer.valueOf(parsedDate[2]);

		// validate year
		if (year < 0) {
			return false;
		}
		// Validate Month
		if (month < 0 || month >12 ) {
			return false;
		}
		//Validate Day
		if (day > 31 ) {
			return false;
		} else if (month == 4 || month == 6 || month == 9 || month == 11 && day > 30){
			return false;
		} else if (month == 2){
			if (day > 28){
				return false;
			} else if (year%4 == 0 && day > 29) {
				return false;
			}
		}

		return true;
	}

}
