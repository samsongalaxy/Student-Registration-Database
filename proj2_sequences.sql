drop sequence logid_sequence;
create sequence logid_sequence
    start with 1001 /*instructions say logid should be 6 digits
    but its declared to 4 digits in prok2_table.sql*/
    increment by 1
    nocache;
    --adds an incrementing ID number for Logs
