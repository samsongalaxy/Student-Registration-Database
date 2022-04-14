set serveroutput on;
create or replace package proj2 as
type ref_cursor is ref cursor;
--2, functions to pass each of the 6 tables as a ref_cursor for proj2.java to output
function show_students return ref_cursor;
function show_courses return ref_cursor;
function show_prerequisites return ref_cursor;
function show_classes return ref_cursor;
function show_enrollments return ref_cursor;
function show_logs return ref_cursor;
--3, takes input to add a student to table
procedure add_student(
    sid_in in students.sid%type,
    fn_in in students.firstname%type,
    ln_in in students.lastname%type,
    status_in in students.status%type,
    gpa_in in students.gpa%type,
    email_in in students.email%type);
--4
function find_student(sid_in in students.sid%type,
                      ln_out out students.lastname%type,
                      status_out out students.lastname%type)
                      return ref_cursor;

--5
function find_prereqs(dc_in in prerequisites.dept_code%type,
                      cn_in in prerequisites.course_no%type)
                      return ref_cursor;

--6
function class_roster(cid_in in classes.classid%type,
                      title_out out courses.title%type)
                      return ref_cursor;

--7
function enroll_student(sid_in in students.sid%type,
                          cid_in in classes.classid%type)
                          return number;
--8
function drop_class(sid_in in students.sid%type,
                          cid_in in classes.classid%type)
                          return number;

--9
function delete_student(sid_in in students.sid%type) return number;
end proj2;
/
show errors
create or replace package body proj2 as
--2
--procedure to print all students
function show_students
return ref_cursor as
rc ref_cursor;
begin
    open rc for
    select * from students
    order by sid;
    return rc;
end;
--procedure to print all courses
function show_courses
return ref_cursor as
rc ref_cursor;
begin
    open rc for
    select * from courses;
    return rc;
end;
--procedure to print all prerequisites
function show_prerequisites
return ref_cursor as
rc ref_cursor;
begin
    open rc for
    select * from prerequisites;
    return rc;
end;
--procedure to print all classes
function show_classes
return ref_cursor as
rc ref_cursor;
begin
    open rc for
    select * from classes
    order by classid;
    return rc;
end;
--procedure to print all enrollments
function show_enrollments
return ref_cursor as
rc ref_cursor;
begin
    open rc for
    select * from enrollments
    order by sid;
    return rc;
end;
--procedure to print all logs
function show_logs
return ref_cursor as
rc ref_cursor;
begin
    open rc for
    select * from logs
    order by logid;
    return rc;
end;

--3, procedure to add a student with input parameters
procedure add_student(
    sid_in in students.sid%type,
    fn_in in students.firstname%type,
    ln_in in students.lastname%type,
    status_in in students.status%type,
    gpa_in in students.gpa%type,
    email_in in students.email%type) is
begin
    insert into students(sid, firstname, lastname, status, gpa, email) values(sid_in, fn_in, ln_in, status_in, gpa_in, email_in);
end;
--4, procedure to output all student information and courses taken when given valid sid_in
function find_student(sid_in in students.sid%type,
                      ln_out out students.lastname%type,
                      status_out out students.lastname%type)
return ref_cursor as
rc ref_cursor;
sid_flag number; --flag to check if the student exists and whether or not they take any classes
begin
    sid_flag := 0;
    select count(*)
    into sid_flag
    from students
    where sid = sid_in;
    if sid_flag = 0 then
      dbms_output.put_line('invalid sid');
    else
      select lastname into ln_out from students where students.sid = sid_in;
      select status into status_out from students where students.sid = sid_in;
      open rc for
      select distinct classes.classid, classes.dept_code, classes.course_no
      from classes, enrollments
      where enrollments.sid = sid_in and classes.classid = enrollments.classid
      order by classes.classid;
      if rc is null then dbms_output.put_line(sid_in || 'has not taken any course');
      end if;
      return rc;
    end if;
    return null;
end;
--5, find prerequisites for given dept_code and course_no
function find_prereqs(dc_in in prerequisites.dept_code%type,
                      cn_in in prerequisites.course_no%type)
return ref_cursor as
rc ref_cursor;
begin
    open rc for
    select distinct pre_dept_code, pre_course_no
    from prerequisites
    where dept_code = dc_in and course_no = cn_in;
    return rc;
end;
--6, print classid, course title, and all students enrolled in class for given classid
function class_roster(cid_in in classes.classid%type,
                      title_out out courses.title%type)
return ref_cursor as
rc ref_cursor;
cid_flag number;
begin
    cid_flag := 0;
    select count(*)
    into cid_flag
    from classes
    where classid = cid_in;
    if cid_flag = 0 then
      dbms_output.put_line('invalid cid');
    else
      select title into title_out from courses, classes
      where courses.dept_code = classes.dept_code and courses.course_no = classes.course_no
        and classes.classid = cid_in;
      open rc for
      select distinct students.sid, lastname, email
      from students, enrollments
      where enrollments.classid = cid_in and students.sid = enrollments.sid
      order by students.sid;
      if rc is null then dbms_output.put_line('empty class');
      end if;
      return rc;
    end if;
    return null;
end;
--7
function enroll_student(sid_in in students.sid%type,
                          cid_in in classes.classid%type)
return number as
n number;
sid_flag number; --flag to check if the student exists
cid_flag number; --flag to check if the class exists
lim number;
csize number;
enrolled number;
class_prereqs number;
taken_prereqs number;
total_enrolled number;
class_year classes.year%type;
class_sem classes.semester%type;
begin
  sid_flag := 0;
  cid_flag := 0;
  enrolled := 0;
  total_enrolled := 0;
  select count(*)
  into sid_flag
  from students
  where sid = sid_in; --check if students exitst
  select count(*)
  into cid_flag
  from classes
  where classid = cid_in; --check if class exists
  if sid_flag = 0 then
    dbms_output.put_line('invalid sid');
    return 1; --error code student not found
  elsif cid_flag = 0 then
    dbms_output.put_line('invalid classid');
    return 2; --error code class not found
  else
    select limit into lim
    from classes
    where classid = cid_in;
    select class_size into csize
    from classes
    where classid = cid_in;
    --check to see if student is currently enrolled in this class
    select count(*) into enrolled
    from enrollments
    where sid = sid_in and classid = cid_in;
    select year into class_year
    from classes
    where classid = cid_in;
    select semester into class_sem
    from classes
    where classid = cid_in;
    select count(*) into total_enrolled
    from enrollments, classes
    where enrollments.sid = sid_in and enrollments.classid = cid_in
          and classes.classid = cid_in and year = class_year and semester = class_sem;
    select distinct count(*)
    into class_prereqs
    from prerequisites, classes
    where classes.dept_code = prerequisites.dept_code and classes.course_no = prerequisites.course_no
      and classes.classid = cid_in;
    select distinct count(*)
    into taken_prereqs
    from prerequisites, classes c1, classes c2, enrollments, courses
    where cid_in = c1.classid and enrollments.classid = c2.classid
        and c2.dept_code = prerequisites.pre_dept_code and c1.dept_code = prerequisites.dept_code
        and c2.course_no = prerequisites.pre_course_no and c1.course_no = prerequisites.course_no
        and (lgrade = 'A' or lgrade = 'B' or lgrade = 'C') and enrollments.sid = sid_in;
    if csize+1 > lim then
      dbms_output.put_line('class full'); --checks if adding student would put class over limit
      return 3; --error code class full
    elsif enrolled > 0 then
      dbms_output.put_line('already in this class');
      return 4; --error code in this class
    elsif total_enrolled > 4 then
      dbms_output.put_line('overloaded!');
      return 5; --error code overload
    elsif taken_prereqs < class_prereqs then
      dbms_output.put_line('prerequisite courses have not been completed.');
      return 6;
    end if;
  end if;
  insert into enrollments(sid, classid, lgrade) values(sid_in, cid_in, null);
  return 0;
end;
--8, drop a student from input class
function drop_class(sid_in in students.sid%type,
                          cid_in in classes.classid%type)
return number as
n number;
sid_flag number;
cid_flag number;
prereq_flag number;
enrolled number;
total_enrolled number;
csize number;
class_year classes.year%type;
class_sem classes.semester%type;
begin
  sid_flag := 0;
  cid_flag := 0;
  enrolled := 0;
  total_enrolled := 0;
  select count(*)
  into sid_flag
  from students
  where sid = sid_in;
  select count(*)
  into cid_flag
  from classes
  where classid = cid_in;
  select class_size into csize
  from classes
  where classid = cid_in;
  if sid_flag = 0 then
    dbms_output.put_line('invalid sid');
    return 1; --error code student not found
  elsif cid_flag = 0 then
    dbms_output.put_line('invalid classid');
    return 2; --error code class not found
  else
    --check to see if student is currently enrolled in this class
    select count(*) into enrolled
    from enrollments
    where sid = sid_in and classid = cid_in;
    select year into class_year
    from classes
    where classid = cid_in;
    select semester into class_sem
    from classes
    where classid = cid_in;
    select count(*) into total_enrolled
    from enrollments, classes
    where enrollments.sid = sid_in;
    select count(*) into prereq_flag
    from enrollments, classes c1, classes c2, prerequisites
    where enrollments.sid = sid_in and c1.classid = cid_in and c2.dept_code = prerequisites.dept_code and c2.course_no = prerequisites.course_no
      and prerequisites.pre_course_no = c1.course_no and prerequisites.pre_dept_code = c1.dept_code and c2.classid = enrollments.classid;
    if enrolled < 0 then
      dbms_output.put_line('student not enrolled');
      return 3; --error code NOT in this class
    elsif total_enrolled < 2 then
      dbms_output.put_line('drop requested rejected; must be enrolled in at least one class');
      return 4; --error code last class
    elsif prereq_flag > 0 then
      dbms_output.put_line('drop request rejected due to prerequisite requirements.');
      return 5;
    end if;
  end if;
  delete from enrollments where sid = sid_in and classid = cid_in;
  if csize = 1 then
      dbms_output.put_line('no student in this class');
      return 6; --class is now Empty
  end if;
  return 0;
end;
--9
function delete_student(sid_in in students.sid%type) return number as
n number;
sid_flag number;
begin
  sid_flag := 0;
  select count(*)
  into sid_flag
  from students
  where sid = sid_in;
  if sid_flag = 0 then
    dbms_output.put_line('sid not found');
    return 1;
  else
    delete from students where sid = sid_in;
    return 0;
  end if;
end;
end proj2;
/
show errors
