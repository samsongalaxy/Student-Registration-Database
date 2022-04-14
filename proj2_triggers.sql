--calls sequence to generate new logid every time a tuple is inserted in table logs
create or replace trigger generate_logid
    before insert on logs
    for each row
begin
    select logid_sequence.nextval
    into :new.logid
    from dual;
end;
/

/*adds a log to table logs every time a student is added to table students.
Tracks timestamp, table added to, operation performed, and SID of student being added.*/
create or replace trigger add_log_insert
    after insert on students
    for each row
begin
    insert into logs(logid, who, time, table_name, operation, key_value)
      values(null, 'sgrimm2', CURRENT_TIMESTAMP, 'students', 'insert', :new.sid);
end;
/

/*adds a log to table logs every time a student is deleted from table students.
Tracks timestamp, table added to, operation performed, and SID of student being deleted.*/
create or replace trigger add_log_delete
    before delete on students
    for each row
begin
    insert into logs(logid, who, time, table_name, operation, key_value)
      values(null, 'sgrimm2', CURRENT_TIMESTAMP, 'students', 'delete', :old.sid);
end;
/

/*adds a log to table logs every time a student is updated in table students.
Tracks timestamp, table added to, operation performed, and SID of student being updated.*/
create or replace trigger add_log_update
    after update on students
    for each row
begin
    insert into logs(logid, who, time, table_name, operation, key_value)
      values(null, 'sgrimm2', CURRENT_TIMESTAMP, 'students', 'update', :new.sid);
end;
/

/*increments class size by 1 every time a student is added to a class.*/
create or replace trigger update_class_size
    after insert on enrollments
    for each row
begin
    update classes
    set class_size = class_size + 1
    where :new.classid = classes.classid;
end;
/

/*decreases class size by 1 every time a student is deleted from a class.*/
create or replace trigger decrease_class_size
    before delete on enrollments
    for each row
begin
    update classes
    set class_size = class_size - 1
    where :old.classid = classes.classid;
end;
/
show errors
