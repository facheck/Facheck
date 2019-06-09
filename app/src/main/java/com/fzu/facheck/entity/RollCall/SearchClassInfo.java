package com.fzu.facheck.entity.RollCall;

import java.util.List;

public class SearchClassInfo {
    public String code;
    private List<ResultClass> resultClasses;
    public static class ResultClass{
        private String className;
        private String classTime;
        private boolean state;
        private String teacherName;
        private String classId;
        public String getClassName(){
            return className;
        }
        public String getClassTime(){
            return classTime;
        }
        public boolean getState(){
            return state;
        }
        public String getClassid() { return classId; }
        public String getTeacherName(){return teacherName;}
    }
    public List<ResultClass> getResultClasses(){
        return resultClasses;
    }
}
