import java.sql.*;
import oracle.jdbc.*;
import java.math.*;
import java.io.*;
import java.awt.*;
import oracle.jdbc.pool.OracleDataSource;



public class proj2 {

   public static void main (String args []) throws SQLException {
    try
    {
        //Connection to Oracle server
        System.out.println("Please enter your Oracle username:");
        BufferedReader readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
        String user = readKeyBoard.readLine();
        System.out.println("Please enter your Oracle password:");
        readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
        String password = readKeyBoard.readLine();
        OracleDataSource ds = new oracle.jdbc.pool.OracleDataSource();
        ds.setURL("jdbc:oracle:thin:@castor.cc.binghamton.edu:1521:ACAD111");
        Connection conn = ds.getConnection(user, password);
        int flag = 0; //program runs while flag < 1
        CallableStatement cs;
        ResultSet rs;
        while(flag < 1) { //displays different functions/procedures as options and waits for user input
            String in, sid, cid;
            readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Select one of the following options (enter the number):");
            System.out.println("1: Display a table");
            System.out.println("2: Add a student");
            System.out.println("3: Enter a SID to find student's information");
            System.out.println("4: Enter a dept_code and course_no to find prerequisites");
            System.out.println("5: Enter a classid to find enrolled students");
            System.out.println("6: Enroll a student in a class");
            System.out.println("7: Drop a student from a class");
            System.out.println("8: Delete a student");
            System.out.println("0: Exit");
            in = readKeyBoard.readLine();
            int i = Integer.parseInt(in);
            switch (i){
                case 0: // sets flag to 1 to break loop and exit program
                    System.out.println("Exiting program...");
                    flag = 1;
                    break;
                case 1: //displays submenu of possible tables to display and waits for input
                    System.out.println("Which table would you like to display?");
                    System.out.println("1: Students");
                    System.out.println("2: Courses");
                    System.out.println("3: Prerequisites");
                    System.out.println("4: Classes");
                    System.out.println("5: Enrollments");
                    System.out.println("6: Logs");
                    System.out.println("0: Return to main menu");
                    readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
                    in = readKeyBoard.readLine();
                    i = Integer.parseInt(in);
                    switch (i){
                        case 0:
                            System.out.println("Returning to main menu...");
                            break;
                        case 1:
                            cs = conn.prepareCall("begin ? := proj2.show_students(); end;");
                            //register the out parameter (the first parameter)
                            cs.registerOutParameter(1, OracleTypes.CURSOR);
                            // execute and retrieve the result set
                            cs.execute();
                            rs = (ResultSet)cs.getObject(1);
                            // print the results
                            while (rs.next()) {
                                System.out.println(rs.getString(1) + "\t" +
                                    rs.getString(2) + "\t" + rs.getString(3) + "\t" +
                                    rs.getString(4) + "\t" + rs.getDouble(5) + "\t" +
                                    rs.getString(6));
                            }
                            cs.close();
                            break;
                        case 2:
                            cs = conn.prepareCall("begin ? := proj2.show_courses(); end;");
                            //register the out parameter (the first parameter)
                            cs.registerOutParameter(1, OracleTypes.CURSOR);
                            // execute and retrieve the result set
                            cs.execute();
                            rs = (ResultSet)cs.getObject(1);
                            // print the results
                            while (rs.next()) {
                                System.out.println(rs.getString(1) + "\t" +
                                    rs.getString(2) + "\t" + rs.getString(3));
                            }
                            cs.close();
                            break;
                        case 3:
                            cs = conn.prepareCall("begin ? := proj2.show_prerequisites(); end;");
                            //register the out parameter (the first parameter)
                            cs.registerOutParameter(1, OracleTypes.CURSOR);
                            // execute and retrieve the result set
                            cs.execute();
                            rs = (ResultSet)cs.getObject(1);
                            // print the results
                            while (rs.next()) {
                                System.out.println(rs.getString(1) +
                                    rs.getString(2) + "\t" + rs.getString(3) + rs.getString(4));
                            }
                            cs.close();
                            break;
                        case 4:
                            cs = conn.prepareCall("begin ? := proj2.show_classes(); end;");
                            //register the out parameter (the first parameter)
                            cs.registerOutParameter(1, OracleTypes.CURSOR);
                            // execute and retrieve the result set
                            cs.execute();
                            rs = (ResultSet)cs.getObject(1);
                            // print the results
                            while (rs.next()) {
                                System.out.println(rs.getString(1) + "\t" +
                                    rs.getString(2) + "\t" + rs.getString(3) + "\t" +
                                    rs.getString(4) + "\t" + (int)rs.getDouble(5) + "\t" +
                                    rs.getString(6) + "\t" + rs.getString(7) + "\t" + rs.getString(8));
                            }
                            cs.close();
                            break;
                        case 5:
                            cs = conn.prepareCall("begin ? := proj2.show_enrollments(); end;");
                            //register the out parameter (the first parameter)
                            cs.registerOutParameter(1, OracleTypes.CURSOR);
                            // execute and retrieve the result set
                            cs.execute();
                            rs = (ResultSet)cs.getObject(1);
                            // print the results
                            while (rs.next()) {
                                System.out.println(rs.getString(1) + "\t" + rs.getString(2) + "\t" + rs.getString(3));
                            }
                            cs.close();
                            break;
                        case 6:
                            cs = conn.prepareCall("begin ? := proj2.show_logs(); end;");
                            //register the out parameter (the first parameter)
                            cs.registerOutParameter(1, OracleTypes.CURSOR);
                            // execute and retrieve the result set
                            cs.execute();
                            rs = (ResultSet)cs.getObject(1);
                            // print the results
                            while (rs.next()) {
                                System.out.println((int)rs.getDouble(1) + "\t" +
                                    rs.getString(2) + "\t" + rs.getString(3) + "\t" +
                                    rs.getString(4) +
                                    "\t" + rs.getString(5) + "\t" + rs.getString(6));
                            }
                            cs.close();
                            break;
                        default:
                            System.out.println("Invalid input, returning to main menu...");
                            break;
                    }
                    break;
                case 2:
                    cs = conn.prepareCall("begin proj2.add_student(?, ?, ?, ?, ?, ?); end;");
                    System.out.println("Please enter the SID: ");
                    readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
                    sid = readKeyBoard.readLine();
                    cs.setString(1, sid);
                    System.out.println("Please enter the first name: ");
                    readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
                    String fn = readKeyBoard.readLine();
                    cs.setString(2, fn);
                    System.out.println("Please enter the last name: ");
                    readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
                    String ln = readKeyBoard.readLine();
                    cs.setString(3, ln);
                    System.out.println("Please enter the status: ");
                    readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
                    String stat = readKeyBoard.readLine();
                    cs.setString(4, stat);
                    System.out.println("Please enter the GPA: ");
                    readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
                    in = readKeyBoard.readLine();
                    double gpa = Double.parseDouble(in);
                    cs.setDouble(5, gpa);
                    System.out.println("Please enter the email: ");
                    readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
                    String email = readKeyBoard.readLine();
                    cs.setString(6, email);
                    cs.executeUpdate();
                    System.out.println("Added student " + sid + " to table.");
                    cs.close();
                    break;
                case 3:
                    readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
                    System.out.print("Please enter SID: ");
                    sid = readKeyBoard.readLine();
                    cs = conn.prepareCall("begin ? := proj2.find_student(?,?,?); end;");
                    cs.registerOutParameter(1, OracleTypes.CURSOR);
                    cs.setString(2, sid);
                    cs.registerOutParameter(3, Types.VARCHAR);
                    cs.registerOutParameter(4, Types.VARCHAR);
                    cs.executeQuery();
                    rs = (ResultSet)cs.getObject(1);
                    if(cs.getString(3) != null){
                      System.out.println("SID: " + sid + " Last Name: " +
                          cs.getString(3) + " Status: " + cs.getString(4));
                      if(!rs.isBeforeFirst()){
                        System.out.println(sid + " hasn't taken any courses.");
                      }
                      else{
                        System.out.println("Classes:");
                        while (rs.next()) {
                            System.out.println(rs.getString(1) +
                                "\t" + rs.getString(2) + rs.getString(3));
                        }
                      }
                    }
                    else System.out.println("Invalid SID, returning to main menu...");
                    break;
                case 4:
                    readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
                    System.out.print("Please enter Deptartment Code: ");
                    String dc = readKeyBoard.readLine();
                    readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
                    System.out.print("Please enter Course Number: ");
                    String cn = readKeyBoard.readLine();
                    cs = conn.prepareCall("begin ? := proj2.find_prereqs(?,?); end;");
                    cs.registerOutParameter(1, OracleTypes.CURSOR);
                    cs.setString(2, dc);
                    cs.setString(3, cn);
                    cs.executeQuery();
                    rs = (ResultSet)cs.getObject(1);
                    while (rs.next()) {
                        System.out.println(rs.getString(1) +  rs.getString(2));
                    }
                    break;
                case 5:
                    readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
                    System.out.print("Please enter Class ID: ");
                    cid = readKeyBoard.readLine();
                    cs = conn.prepareCall("begin ? := proj2.class_roster(?,?); end;");
                    cs.registerOutParameter(1, OracleTypes.CURSOR);
                    cs.setString(2, cid);
                    cs.registerOutParameter(3, Types.VARCHAR);
                    cs.executeQuery();
                    rs = (ResultSet)cs.getObject(1);
                    if(cs.getString(3) != null){
                      System.out.println("Class ID: " + cid + " Course Title: " +
                          cs.getString(3));
                      if(!rs.isBeforeFirst()){
                        System.out.println("Empty class, returning to main menu...");
                      }
                      else{
                        System.out.println("Students:");
                        while (rs.next()) {
                            System.out.println(rs.getString(1) +
                                "\t" + rs.getString(2) + rs.getString(3));
                        }
                      }
                    }
                    else System.out.println("Invalid Class ID, returning to main menu...");
                    break;
                case 6:
                    readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
                    System.out.print("Please enter SID: ");
                    sid = readKeyBoard.readLine();
                    readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
                    System.out.print("Please enter Class ID: ");
                    cid = readKeyBoard.readLine();
                    cs = conn.prepareCall("begin ? := proj2.enroll_student(?,?); end;");
                    cs.registerOutParameter(1, Types.NUMERIC);
                    cs.setString(2, sid);
                    cs.setString(3, cid);
                    cs.executeQuery();

                    switch((int)cs.getDouble(1)){
                      case 1: System.out.println("Invalid SID, returning to main menu...");
                          break;
                      case 2: System.out.println("Invalid Class ID, returning to main menu...");
                          break;
                      case 3: System.out.println("Class is full, returning to main menu...");
                          break;
                      case 4: System.out.println("Student is already enrolled in this class, returning to main menu...");
                          break;
                      case 5: System.out.println("Student is already enrolled in 5 classes, returning to main menu...");
                          break;
                      case 6: System.out.println("Student is missing required prerequisite(s), returning to main menu...");
                          break;
                      default: System.out.println(sid + " enrolled in " + cid + " successfully, returning to main menu...");
                    }
                    break;
                case 7:
                    readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
                    System.out.print("Please enter SID: ");
                    sid = readKeyBoard.readLine();
                    readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
                    System.out.print("Please enter Class ID: ");
                    cid = readKeyBoard.readLine();
                    cs = conn.prepareCall("begin ? := proj2.drop_class(?,?); end;");
                    cs.registerOutParameter(1, Types.NUMERIC);
                    cs.setString(2, sid);
                    cs.setString(3, cid);
                    cs.executeQuery();
                    switch((int)cs.getDouble(1)){
                      case 1: System.out.println("Invalid SID, returning to main menu...");
                          break;
                      case 2: System.out.println("Invalid Class ID, returning to main menu...");
                          break;
                      case 3: System.out.println("Student not enrolled in class, returning to main menu...");
                          break;
                      case 4: System.out.println("Drop requested rejected; student must be enrolled in at least one class, returning to main menu...");
                          break;
                      case 5: System.out.println("Drop requested rejected; class is required as prerequisite for other class student is enrolled in.\nReturning to main menu...");
                          break;
                      case 6: System.out.println(sid + " dropped " + cid + " successfully.\nNo students remain in this class, returning to main menu...");
                          break;
                      default: System.out.println(sid + " dropped " + cid + " successfully, returning to main menu...");
                    }
                    break;
                case 8:
                    readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
                    System.out.print("Please enter SID: ");
                    sid = readKeyBoard.readLine();
                    cs = conn.prepareCall("begin ? := proj2.delete_student(?); end;");
                    cs.registerOutParameter(1, Types.NUMERIC);
                    cs.setString(2, sid);
                    cs.executeUpdate();
                    if(cs.getDouble(1) == 0){
                      System.out.println("Student " + sid + " deleted successfully, returning to main menu...");
                    }
                    else System.out.println("Invalid SID, returning to main menu...");
                    break;
                default: System.out.println("Please select a valid option.");
            }
        }

        conn.close();
   }
   catch (SQLException ex) { System.out.println ("\n*** SQLException caught ***\n" + ex.getMessage());}
   catch (Exception e) {System.out.println ("\n*** other Exception caught ***\n");}
  }
}
