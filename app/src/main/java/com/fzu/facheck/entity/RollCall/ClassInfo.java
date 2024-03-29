package com.fzu.facheck.entity.RollCall;

import java.util.ArrayList;
import java.util.List;

public class ClassInfo {
    public String code;
    public String startTime;
    public String endTime;
    public String classCode;
    public String teacherName;
    public String classAttendanceRate;
    private List<Student> studentList;
    private List<Record> recordList;
    public static class Student{
        private String phoneNumber;
        private String name;
        private String studentId;
        public Student(String id,String name){
            this.phoneNumber=id;
            this.name=name;
        }
        public String getName(){
            return name;
        }
        public String getPhoneNumber(){
            return phoneNumber;
        }
        public String getStudentId(){return studentId;}
    }
    public static class Record{
        private String recordId;
        private String time;
        private String attendanceRatio;
        private String attendanceRate;

        public Record(String recordid,String time,String attendanceRatio,String attendanceRate){
            this.time=time;
            this.attendanceRatio=attendanceRatio;
            this.recordId=recordid;
            this.attendanceRate = attendanceRate;
        }
        public String getTime(){
            return time;
        }
        public String getAttendanceRatio(){
            return attendanceRatio;
        }
        public String getRecordId(){return recordId;}
        public String getAttendanceRate(){return attendanceRate;}

    }
    public List<Student> getStudents(){
        List<Student> st=new ArrayList<Student>();
        if(studentList==null)
            return st;
        else
            return studentList;
    }
    public List<Record> getRecords(){
        List<Record> re=new ArrayList<Record>();
        if(recordList==null)
            return re;
        else
            return recordList;
    }
}
